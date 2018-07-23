/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package tools.descartes.teastore.image.storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Predicate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tools.descartes.teastore.entities.ImageSize;
import tools.descartes.teastore.image.ImageDB;
import tools.descartes.teastore.image.StoreImage;

/**
 * Storage implementation that saves data in a directory on the physical disc.
 * @author Norbert Schmitt
 */
public class DriveStorage implements IDataStorage<StoreImage> {

  private Path workingDir;
  private ImageDB imgDB;
  private Predicate<StoreImage> storageRule;
  private Logger log = LoggerFactory.getLogger(DriveStorage.class);

  private final HashMap<Long, ReadWriteLock> lockedIDs = new HashMap<>();
  private final ReadWriteLock mapLock = new ReentrantReadWriteLock();

  /**
   * Standard constructor creating a storage in the working directory on the physical disc.
   * @param workingDir Working directory on the physical disc where the data is stored.
   * @param imgDB Image database containing the IDs for the data.
   * @param storageRule Storage rule which data can be stored.
   */
  public DriveStorage(Path workingDir, ImageDB imgDB, Predicate<StoreImage> storageRule) {
    if (workingDir == null) {
      log.error("The supplied working directory is null.");
      throw new NullPointerException("The supplied working directory is null.");
    }
    if (imgDB == null) {
      log.error("The supplied image database is null.");
      throw new NullPointerException("The supplied image database is null.");
    }
    if (storageRule == null) {
      log.error("The supplied rule to determine if an image can be stored is null.");
      throw new NullPointerException(
          "The supplied rule to determine if an image can be stored is null.");
    }

    this.workingDir = workingDir.normalize();
    this.imgDB = imgDB;
    this.storageRule = storageRule;
  }

  @Override
  public boolean dataExists(long id) {
    return workingDir.resolve(Long.toString(id)).toFile().exists();
  }

  private ReadWriteLock getIDLock(long id) {
    ReadWriteLock l = null;
    mapLock.writeLock().lock();
    try {
      if (lockedIDs.containsKey(id)) {
        l = lockedIDs.get(id);
      } else {
        l = new ReentrantReadWriteLock();
        lockedIDs.put(id, l);
      }
    } finally {
      mapLock.writeLock().unlock();
    }
    return l;
  }

  /**
   * Returns the stored image on the physical disc for a given image id at the given path.
   * @param imgFile Image file path to load binary data.
   * @param id Image id to acquire the correct lock prohibiting write access to the file.
   * @return The image stored on disc or NULL if an IOException occurred during reading.
   */
  protected StoreImage loadFromDisk(Path imgFile, long id) {
    byte[] imgData = null;

    // Try aquiring a lock for a file.
    ReadWriteLock l = getIDLock(id);
    l.readLock().lock();
    try {
      imgData = Files.readAllBytes(imgFile);
    } catch (IOException ioException) {
      log.warn("An IOException occured while trying to read the file \"" + imgFile.toAbsolutePath()
          + "\" from disk. Returning null.", ioException);
    } finally {
      l.readLock().unlock();
    }

    if (imgData == null) {
      return null;
    }

    ImageSize size = imgDB.getImageSize(id);
    if (size == null) {
      return null;
    }

    return new StoreImage(id, imgData, size);
  }

  @Override
  public StoreImage loadData(long id) {
    Path imgFile = workingDir.resolve(Long.toString(id));
    if (!imgFile.toFile().exists()) {
      return null;
    }

    return loadFromDisk(imgFile, id);
  }

  @Override
  public boolean saveData(StoreImage data) {
    // We return true so we do not trigger an error. This is intended
    if (!dataIsStorable(data)) {
      return true;
    }

    Path imgFile = workingDir.resolve(Long.toString(data.getId()));
    if (imgFile.toFile().exists()) {
      return true;
    }

    ReadWriteLock l = getIDLock(data.getId());
    l.writeLock().lock();

    try {
      Files.write(imgFile, data.getByteArray(), StandardOpenOption.CREATE, StandardOpenOption.WRITE,
          StandardOpenOption.TRUNCATE_EXISTING);
    } catch (IOException ioException) {
      log.warn("An IOException occured while trying to write the file \"" + imgFile.toAbsolutePath()
          + "\" to disk.", ioException);
      return false;
    } finally {
      l.writeLock().unlock();
    }

    return true;
  }

  @Override
  public boolean dataIsStorable(StoreImage data) {
    return storageRule.test(data);
  }

  @Override
  public boolean deleteData(StoreImage data) {
    Path imgFile = workingDir.resolve(Long.toString(data.getId()));
    if (!imgFile.toFile().exists()) {
      return true;
    }

    boolean result = false;

    ReadWriteLock l = getIDLock(data.getId());
    l.writeLock().lock();
    try {
      result = imgFile.toFile().delete();
    } finally {
      l.writeLock().unlock();
    }

    return result;
  }

}
