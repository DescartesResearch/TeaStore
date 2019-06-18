package tools.descartes.teastore.webui.cart;

import tools.descartes.research.faasteastorelibrary.interfaces.persistence.CartItemEntity;

import java.util.LinkedList;
import java.util.List;

public class CartManagerSingleton
{
    private static CartManagerSingleton instance;

    private List< CartItemEntity > cartItems;

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

    public void addCartItem( final CartItemEntity cartItem )
    {
        this.cartItems.add( cartItem );
    }

    public List< CartItemEntity > getCartItems( )
    {
        return this.cartItems;
    }

    public void deleteCartItems( )
    {
        this.cartItems = new LinkedList<>( );
    }
}