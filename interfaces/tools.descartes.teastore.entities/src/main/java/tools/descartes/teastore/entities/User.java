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
package tools.descartes.teastore.entities;

/**
 * User for the pet supply store.
 * 
 * @author Joakim von Kistowski
 *
 */
public class User {

  private long id;
  private String userName;
  private String password;
  private String realName;
  private String email;

  /**
   * Create a new and empty user.
   */
  public User() {

  }

  /**
   * Every entity needs a copy constructor.
   * 
   * @param user
   *          The user to copy.
   */
  public User(User user) {
    setId(user.getId());
    setUserName(user.getUserName());
    setPassword(user.getPassword());
    setRealName(user.getRealName());
    setEmail(user.getEmail());
  }

  /**
   * Get the id (remember that this ID may be incorrect, especially if a separate id was passed).
   * 
   * @return The id.
   */
  public long getId() {
    return id;
  }

  /**
   * For REST use only. Sets the ID. Ignored by persistence.
   * 
   * @param id
   *          ID, as passed by the REST API.
   */
  public void setId(long id) {
    this.id = id;
  }

  /**
   * Get the Username.
   * 
   * @return the userName.
   */
  public String getUserName() {
    return userName;
  }

  /**
   * Set the username, should only be set at creation.
   * 
   * @param userName
   *          the userName to set.
   */
  public void setUserName(String userName) {
    this.userName = userName;
  }

  /**
   * Get the password hash.
   * 
   * @return the password hash.
   */
  public String getPassword() {
    return password;
  }

  /**
   * Set the password hash.
   * 
   * @param password
   *          the password hash to set.
   */
  public void setPassword(String password) {
    this.password = password;
  }

  /**
   * Get the user's real name.
   * 
   * @return the realName.
   */
  public String getRealName() {
    return realName;
  }

  /**
   * Set the user's real name.
   * 
   * @param realName
   *          the realName to set.
   */
  public void setRealName(String realName) {
    this.realName = realName;
  }

  /**
   * Get the user's email address.
   * 
   * @return the email.
   */
  public String getEmail() {
    return email;
  }

  /**
   * Set the user's email address.
   * 
   * @param email
   *          the email to set.
   */
  public void setEmail(String email) {
    this.email = email;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + (int) (id ^ (id >>> 32));
    if (userName == null) {
      result = prime * result + 0;
    } else {
      result = prime * result + userName.hashCode();
    }
    return result;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    User other = (User) obj;
    if (id != other.id) {
      return false;
    }
    if (userName == null) {
      if (other.userName != null) {
        return false;
      }
    } else {
      if (!userName.equals(other.userName)) {
        return false;
      }
    }
    return true;
  }

}
