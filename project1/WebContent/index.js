// let cart = $("#cart");
//let mlist = $("#mlist");
/**
 * Handle the data returned by IndexServlet
 * @param resultDataString jsonObject, consists of session info
 */

let browse = $("#browse");
let search = $("#search");


/**
 * Handle the items in item list
 * @param resultDataString jsonObject, needs to be parsed to html
 */
// function handleCartArray(resultDataString) {
//     const resultArray = resultDataString.split(",");
//     console.log(resultArray);
//     let item_list = $("#item_list");
//     // change it to html list
//     let res = "<ul>";
//     for (let i = 0; i < resultArray.length; i++) {
//         // each item will be in a bullet point
//         res += "<li>" + resultArray[i] + "</li>";
//     }
//     res += "</ul>";
//
//     // clear the old array and show the new array in the frontend
//     item_list.html("");
//     item_list.append(res);
// }

/**
 * Submit form content with POST method
 * @param cartEvent
 */
// function handleCartInfo(cartEvent) {
//     console.log("submit cart form");
//     /**
//      * When users click the submit button, the browser will not direct
//      * users to the url defined in HTML form. Instead, it will call this
//      * event handler when the event is triggered.
//      */
//     cartEvent.preventDefault();
//
//     $.ajax("api/index", {
//         method: "POST",
//         data: cart.serialize(),
//         success: handleCartArray
//     });
// }

function logout() {
    window.location.replace("login.html");
}

function search_redir() {
    window.location.replace("search-setup.html");
}

function browse_redir() {
    window.location.replace("browse");
}

// function handleMovieListRedirect(){
//     console.log("redirecting from index to movie-list");
//     window.location.replace("movie-list");
// }

function handleMovieListInfo(mlistEvent) {
    console.log("submit mlist form");
    /**
     * When users click the submit button, the browser will not direct
     * users to the url defined in HTML form. Instead, it will call this
     * event handler when the event is triggered.
     */
    mlistEvent.preventDefault();
    let s1 = document.getElementById("order");
    let s1val = s1.options[s1.selectedIndex].value;
    let s2 = document.getElementById("dir");
    let s2val = s2.options[s2.selectedIndex].value;
    let s21 = document.getElementById("dir2");
    let s21val = s21.options[s21.selectedIndex].value;
    let s3 = document.getElementById("limit");
    let s3val = s3.options[s3.selectedIndex].value;
    let s4 = document.getElementById("offset");
    let s4val = s4.options[s4.selectedIndex].value;
    window.location.replace("movie-list?order=" + s1val + "&dir=" + s2val + "&dir2=" + s21val + "&limit=" + s3val + "&offset=" + s4val);
    // $.ajax("movie-list", {
    //     method: "GET",
    //     data: mlist.serialize(),
    //     success: handleMovieListRedirect
    // });
}

// $.ajax("api/index", {
//     method: "POST",
//     data: cart.serialize(),
//     success: handleCartArray
// });
// $.ajax("api/index", {
//     method: "GET",
//     success: handleSessionData
// });

function handleBrowseRedirect(browseEvent){
    console.log("redirecting from index to browse");
    browseEvent.preventDefault();
    // window.location.replace("browse");
}

function handleSearchRedirect(searchEvent){
    console.log("redirecting from index to search");
    searchEvent.preventDefault();
    //window.location.replace("search-setup.html");
}

// function handleSearchSample(searchEvent){
//     console.log("search sample");
//     searchEvent.preventDefault();
// }

// Bind the submit action of the form to a event handler function
// cart.submit(handleCartInfo);
// mlist.submit(handleMovieListInfo);
browse.submit(handleBrowseRedirect);
search.submit(handleSearchRedirect);