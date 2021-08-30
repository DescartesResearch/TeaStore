describe("Shopping cart", () => {

    beforeEach(() => {
        cy.visit("/tools.descartes.teastore.webui/cart")
    })

    it("should display an empty cart", () => {
        cy.contains("Your cart is empty.")
    })

    it("should add a product to the card", () => {
        addToCart(7)
        cy.contains("Total: $75.82")
    })

    it("should remove the product", () => {
        addToCart(7)
        cy.get(".glyphicon-trash").click()
        cy.contains("Your cart is empty.")
    })

    it("should update the quantity", () => {
        addToCart(7)
        cy.get("input").get(".quantity").clear().type(2)
        cy.contains("Update Cart").click()
        cy.contains("Your cart is updated!")
        cy.contains("Total: $151.64")
    })

    it("should add different products to the cart", () => {
        const price7 = 21.87
        const price8 = 75.82
        addToCart(7)
        addToCart(8)
        
        const totalPrice = Math.round((price7 + price8) * 100) / 100
        cy.contains("Total: $" + totalPrice)
    })

    it("should display reccomendations when card is not empty", () => {
        const reccomenderText = "Are you interested in?"
        cy.contains(reccomenderText).should('not.exist')
        addToCart(7)
        cy.contains(reccomenderText)
    })

    it("should proceed to checkout", () => {
        cy.login()
        addToCart(7)
        cy.contains("Proceed to Checkout").click()
        cy.contains("Order").click()
        cy.contains("Confirm").click()
        cy.contains("Your order is confirmed! ")

    })

    function addToCart(productId){
        cy.visit("/tools.descartes.teastore.webui/product?id="+productId)
        cy.contains("Add to Cart").click()
    }
})