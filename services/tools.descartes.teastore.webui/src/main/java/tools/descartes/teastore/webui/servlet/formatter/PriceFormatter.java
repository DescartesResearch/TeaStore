package tools.descartes.teastore.webui.servlet.formatter;

public class PriceFormatter
{
    private final String dollarSign = "&#36;";

    public PriceFormatter( ) {}

    public String format( final long priceInCents )
    {
        return this.dollarSign + " " + ( ( double ) priceInCents / 100.0 );
    }
}