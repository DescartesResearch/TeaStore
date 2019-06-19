package tools.descartes.teastore.webui.cart;

import tools.descartes.research.faasteastorelibrary.interfaces.persistence.CartItem;

import java.util.LinkedList;
import java.util.List;

public class CartManagerSingleton
{
    private static CartManagerSingleton instance;

    private List< CartItem > cartItems;

    private CartManagerSingleton( )
    {
        this.cartItems = new LinkedList<>( );
    }

    public static CartManagerSingleton getInstance( )
    {
        if ( CartManagerSingleton.instance == null )
        {
            CartManagerSingleton.instance = new CartManagerSingleton( );
        }

        return CartManagerSingleton.instance;
    }

    public void addCartItem( final CartItem cartItem )
    {
        this.cartItems.add( cartItem );
    }

    public List< CartItem > getCartItems( )
    {
        return this.cartItems;
    }

    public void deleteCartItem( final long productId )
    {
        for ( CartItem cartItem : this.cartItems )
        {
            if ( cartItem.getProduct( ).getId( ) == productId )
            {
                this.cartItems.remove( cartItem );
            }
        }
    }

    public void deleteCartItems( )
    {
        this.cartItems = new LinkedList<>( );
    }
}