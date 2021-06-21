package tools.descartes.teastore.registryclient.rest;

import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.PrintWriter;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.WriteListener;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;

/**
 * Warper for responses.
 * 
 * @author Simon
 *
 */
public class CharResponseWrapper extends HttpServletResponseWrapper {
  private CharArrayWriter output;

  /**
   * Returns string content.
   * 
   * @return string
   */
  public String toString() {
    return output.toString();
  }

  /**
   * Constructor using a response.
   * 
   * @param response
   *          response to wrap
   */
  public CharResponseWrapper(HttpServletResponse response) {
    super(response);
    output = new CharArrayWriter();
  }

  /**
   * Getter for print writer.
   * 
   * @return print writer
   */
  public PrintWriter getWriter() {
    return new PrintWriter(output);
  }

  /**
   * Getter for output stream.
   * 
   * @return ServletOutputStream
   */
  @Override
  public ServletOutputStream getOutputStream() throws IOException {
    // This is the magic to prevent closing stream, create a "virtual" stream that
    // does nothing..
    return new ServletOutputStream() {
      @Override
      public void write(int b) throws IOException {
        output.write(b);
      }

      @Override
      public void setWriteListener(WriteListener writeListener) {
      }

      @Override
      public boolean isReady() {
        return true;
      }
    };
  }
}