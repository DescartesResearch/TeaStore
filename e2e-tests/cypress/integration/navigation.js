describe("Navigation", () => {

    beforeEach(() => {
        cy.visit('/')
    })

    it('should navigate to the landing page', () => {
      cy.contains("TeaStore")
    })
  
    it('should navigate to login page', () => {
      cy.contains("Sign in").click()
      cy.contains("h2","Login")
    })
  
    it('should navigate to Black Tea category', () => {
      cy.contains("Black Tea").click()
      cy.contains("h2","Black Tea")
    })

    it('should navigate to shopping cart', () => {
      cy.get(".glyphicon-shopping-cart").click()
      cy.contains("h2","Shopping Cart")
    })


    it('should navigate to about us', () => {
        cy.contains("About us").click()
        cy.contains("Developer Team")
    })

    it('should navigate to status', () => {
        cy.contains("Status").click()
        cy.contains("TeaStore Service Status")
    })

    it('should navigate to database', () => {
        cy.contains("Database").click()
        cy.contains("Setup the Database")
    })


    it('should navigate to user profile', () => {
        cy.login()
        cy.get(".glyphicon-user").click()
        cy.contains("User Information")
        cy.contains("Orders")
    })
    
  })
