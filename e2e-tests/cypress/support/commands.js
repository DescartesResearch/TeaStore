Cypress.Commands.add('login', () => {
    return cy.url().then((url) => {
        cy.visit('/tools.descartes.teastore.webui/login')
        cy.get('input[name="username"]').clear().type('user1')
        cy.get('input[name="password"]').clear().type('password')
        cy.get('input[type="submit"]').click()
        cy.visit(url)
    })
})
  