describe("Categories", () => {

    beforeEach(() => {
        cy.visit('/tools.descartes.teastore.webui/category?category=3&page=1')
    })

    it('should display cart boxes', () => {
        cy.contains("Sencha (loose)")
        cy.contains("Price")
        cy.contains("Add to Cart")
    })

    it('should show next page', () => {
        cy.scrollTo('bottom')
        cy.get(".pagination").contains("next").click()
        cy.scrollTo('bottom')
        cy.contains("previous")
        cy.url().should('include', 'page=2')
    })

    it('should change the number of displayed items to 5', () => {
        const itemsSelect = cy.get("#formpages").get("select")
        itemsSelect.select("5")
        cy.scrollTo('bottom')
        cy.get('input[name="addToCart"]').should('have.length', 5)
    })

    it('should change the number of displayed items to 50', () => {
        const itemsSelect = cy.get("#formpages").get("select")
        itemsSelect.select("50")
        cy.scrollTo('bottom')
        cy.get('input[name="addToCart"]').should('have.length', 50)
    })

    it('should navigate to detail page', () => {
        cy.get('img[alt="Sencha (loose)"').click()
        cy.contains("h2","Sencha (loose)")
    })

})