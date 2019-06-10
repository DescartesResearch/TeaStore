package tools.descartes.teastore.webui.authentication;

import tools.descartes.research.faasteastorelibrary.interfaces.persistence.UserEntity;

public class AuthenticatorSingleton
{
    private UserEntity user;

    private static AuthenticatorSingleton instance;

    private AuthenticatorSingleton( ) {}

    public static AuthenticatorSingleton getInstance( )
    {
        if ( AuthenticatorSingleton.instance == null )
        {
            AuthenticatorSingleton.instance = new AuthenticatorSingleton( );
        }
        return AuthenticatorSingleton.instance;
    }

    public void setUser( final UserEntity user )
    {
        this.user = user;
    }

    public UserEntity getUser( )
    {
        return this.user;
    }

    public void logOutUser( )
    {
        this.user = null;
    }

    public boolean isUserLoggedIn( )
    {
        return this.user != null;
    }
}