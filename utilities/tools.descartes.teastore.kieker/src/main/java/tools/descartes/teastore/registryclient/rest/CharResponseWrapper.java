package tools.descartes.teastore.registryclient.rest;

import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

public class CharResponseWrapper extends HttpServletResponseWrapper {
    private CharArrayWriter output;

    public String toString() {
        return output.toString();
    }

    public CharResponseWrapper(HttpServletResponse response) {
        super(response);
        output = new CharArrayWriter();
    }

    public PrintWriter getWriter() {
        return new PrintWriter(output);
    }
    
    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        //This is the magic to prevent closing stream, create a "virtual" stream that does nothing..
        return new ServletOutputStream() {
            @Override
            public void write(int b) throws IOException {
            	output.write(b);
            }
            @Override
            public void setWriteListener(WriteListener writeListener) {}
            @Override
            public boolean isReady() {
                return true;
            }
        };
    }
}