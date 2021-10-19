const queries = {};

function processQueryParameters() {
    const searchQuery = window.location.search;

    if (!searchQuery) {
        return;
    }

    const queryStrings = searchQuery.substring(1).split('&');
    queryStrings.forEach(function(queryString) {
        const [key, value] = queryString.split('=');
        queries[key] = value;
    });
}

function addRefreshInterval() {

    if (!(queries['refreshDuration'])) {
        return;
    }
    const refreshDuration = parseInt(queries['refreshDuration'], 10);

    setInterval(function() {
        window.location.href = window.location.href;
    }, refreshDuration * 1000);

}

const refreshForm = document.querySelector("#refreshForm");

refreshForm.addEventListener('submit', function(event) {
    event.preventDefault();
    const formValues = event.target.elements;
    const refreshDuration = formValues.refreshDurationField.value;

    if (!refreshDuration) {
        alert("Please enter a valid refresh duration");
        return;
    }

    const redirectUrl = `${window.location.href}?refreshDuration=${refreshDuration}`;
    window.location.href = redirectUrl;
});

processQueryParameters();
addRefreshInterval();