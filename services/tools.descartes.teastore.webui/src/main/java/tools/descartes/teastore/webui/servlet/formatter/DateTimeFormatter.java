package tools.descartes.teastore.webui.servlet.formatter;

public class DateTimeFormatter
{
    public DateTimeFormatter( ) {}

    public String format( final String orderTimeAsIsoTimestamp )
    {
        return orderTimeAsIsoTimestamp.replace( "T", " " );
    }
}