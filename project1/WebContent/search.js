let search_form = $("#search_form");

function handleLookup(query, doneCallback) {
    console.log("autocomplete initiated")
    console.log("sending AJAX request to backend Java Servlet")

    // sending the HTTP GET request to the Java Servlet endpoint hero-suggestion
    // with the query data

    // TODO: if you want to check past query results first, you can do it here
    if (sessionStorage.getItem(query)) {
        console.log("found query in cache");
        //handleLookupAjaxSuccess(sessionStorage.getItem(query), query, doneCallback)
    }
    //else {
    jQuery.ajax({
        "method": "GET",
        // generate the request url from the query.
        // escape the query string to avoid errors caused by special characters
        "url": "movie-suggestion?query=" + escape(query),
        "success": function (data) {
            // pass the data, query, and doneCallback function into the success handler
            handleLookupAjaxSuccess(data, query, doneCallback)
        },
        "error": function (errorData) {
            console.log("lookup ajax error");
            console.log(errorData);
        }
    })
    //}
}

function handleLookupAjaxSuccess(data, query, doneCallback) {
    console.log("lookup successful");

    // parse the string into JSON
    var jsonData = JSON.parse(data);
    console.log(jsonData);

    // TODO: if you want to cache the result into a global variable you can do it here
    sessionStorage.setItem(query, jsonData);

    // call the callback function provided by the autocomplete library
    // add "{suggestions: jsonData}" to satisfy the library response format according to
    //   the "Response Format" section in documentation
    doneCallback( { suggestions: jsonData } );
}

function handleSelectSuggestion(suggestion) {
    // TODO: jump to the specific result page based on the selected suggestion

    console.log("you select " + suggestion["value"] + " with ID " + suggestion["data"]["id"])
    window.location.replace("single-movie?sbit=1&id=" + suggestion["data"]["id"] + "&mtitle=" + suggestion["data"]["title"]);
}

$('#title').autocomplete({
    // documentation of the lookup function can be found under the "Custom lookup function" section
    lookup: function (query, doneCallback) {
        handleLookup(query, doneCallback)
    },
    onSelect: function(suggestion) {
        handleSelectSuggestion(suggestion)
    },
    // set delay time
    deferRequestBy: 300,
    minChars: 3,
    lookupLimit: 10,
    //noCache: true,
    // there are some other parameters that you might want to use to satisfy all the requirements
    // TODO: add other parameters, such as minimum characters
});

$('#title').keypress(function(event) {
    // keyCode 13 is the enter key
    if (event.keyCode == 13) {
        // pass the value of the input box to the handler function
        handleSearchParameters()
    }
})




function handleSearchParameters(searchFormEvent) {
    console.log("submit search form");
    searchFormEvent.preventDefault();
    let s1 = document.getElementById("title");
    let s1val = s1.value;
    let s2 = document.getElementById("year");
    let s2val = s2.value;
    let s3 = document.getElementById("director");
    let s3val = s3.value;
    let s4 = document.getElementById("star");
    let s4val = s4.value;
    let s5 = document.getElementById("order");
    let s5val = s5.options[s5.selectedIndex].value;
    let s6 = document.getElementById("dir");
    let s6val = s6.options[s6.selectedIndex].value;
    let s7 = document.getElementById("dir2");
    let s7val = s7.options[s7.selectedIndex].value;
    let s8 = document.getElementById("limit");
    let s8val = s8.options[s8.selectedIndex].value;
    let s9 = document.getElementById("offset");
    let s9val = s9.options[s9.selectedIndex].value;
    let url = "search?sbit=1&title=" + s1val + "&year=" + s2val + "&director=" + s3val + "&star=" + s4val+ "&order=" + s5val + "&dir=" + s6val + "&dir2=" + s7val + "&limit=" + s8val + "&offset=" + s9val;
    window.location.replace(url);
}

search_form.submit(handleSearchParameters);