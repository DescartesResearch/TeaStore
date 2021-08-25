# TeaStore E2E Tests

## Running the tests
1. Install dependencies (if not already done): `npm install`
2. Start application (e.g. with docker): `cd ../examples/docker && docker-compose -f docker-compose_default.yaml up`
3. Run the tests: `npm run cy:run`

To run the tests in headed mode:
`npm run cy:run:headed`

To open the Cypress Test Runner:
`npm run cy:open`