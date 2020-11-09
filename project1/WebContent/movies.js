let mlist = $("#mlist");

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
    // $.ajax("movie-list?order=" + s1val + "&dir=" + s2val + "&dir2=" + s21val + "&limit=" + s3val + "&offset=" + s4val", {
    //     method: "GET",
    //     success: handleMovieListRedirect
    // });
}

mlist.submit(handleMovieListInfo);