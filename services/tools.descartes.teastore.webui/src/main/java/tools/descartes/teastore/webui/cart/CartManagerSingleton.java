package tools.descartes.teastore.webui.cart;

import tools.descartes.research.faasteastorelibrary.interfaces.persistence.CartItem;
import tools.descartes.research.faasteastorelibrary.interfaces.persistence.ProductEntity;
import tools.descartes.research.faasteastorelibrary.requests.product.GetProductByIdRequest;

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

    public void addCartItem( final long productId )
    {
        boolean isProductAlreadyInCart = false;

        for ( CartItem cartItem : this.cartItems )
        {
            if ( cartItem.getProduct( ).getId( ) == productId )
            {
                isProductAlreadyInCart = true;

                cartItem.setQuantity( cartItem.getQuantity( ) + 1 );

                break;
            }
        }

        if ( !isProductAlreadyInCart )
        {
            CartItem cartItem = createNewCartItem( productId );

            this.cartItems.add( cartItem );
        }
    }

    private CartItem createNewCartItem( final long productId )
    {
        ProductEntity product = getProductById( productId );

        CartItem cartItem = new CartItem( );
        cartItem.setProduct( product );
        cartItem.setQuantity( 1 );

        return cartItem;
    }

    private ProductEntity getProductById( final long productId )
    {
        return new GetProductByIdRequest( productId ).performRequest( ).getParsedResponseBody( );
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

                break;
            }
        }
    }

    public void deleteCartItems( )
    {
        this.cartItems = new LinkedList<>( );
    }
}