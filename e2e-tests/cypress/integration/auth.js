describe("Authentication", () => {

    beforeEach(() => {
        cy.visit('/tools.descartes.teastore.webui/login')
    })

    it('should successfully sign in the user', () => {
      cy.get('input[name="username"]').clear().type('user1')
      cy.get('input[name="password"]').clear().type('password')
      cy.get('input[type="submit"]').click()
      cy.contains("You are logged in!")
    })
  
    it('should show a warning on wrong credetials', () => {
      cy.get('input[name="username"]').clear().type('wrong_user')
      cy.get('input[name="password"]').clear().type('wrong_pw')
      cy.get('input[type="submit"]').click()
      cy.contains("You used wrong credentials!")
    })

    it('should log the user out', () => {
      cy.login()
      cy.contains('Logout').click()
      cy.contains("You are logged out! ")
    })

  })