/*
let desc_form = $("desc_form");


function handleTableInfo(descEvent) {
    console.log("submit desc form");
    descEvent.preventDefault();
    let select_table = document.getElementById("table");
    let table = select_table.options[select_table.selectedIndex].value;
    describeTable(table);
}

desc_form.submit(handleTableInfo);
*/
let star_form = $("#star_form");
let movie_form = $("#movie_form");

function handleStarFormResult(resultDataString) {
    let resultDataJson = JSON.parse(resultDataString);
    if (resultDataJson["status"] === "success")
        alert("Star successfully created.");
    else
        alert("Star creation failed.");
}

function submitStarForm(starEvent) {
    console.log("submit star form");
    starEvent.preventDefault();

    $.ajax(
        "create-star", {
            method: "POST",
            data: star_form.serialize(),
            success: handleStarFormResult
        }
    );
}

function handleMovieFormResult(resultDataString) {
    let resultDataJson = JSON.parse(resultDataString);
    if (resultDataJson["status"] === "success")
        alert("Movie successfully created.");
    else
        alert("Movie creation failed.");
}

function submitMovieForm(movieEvent) {
    console.log("submit movie form");
    movieEvent.preventDefault();

    $.ajax(
        "create-movie", {
            method: "POST",
            data: movie_form.serialize(),
            success: handleMovieFormResult
        }
    );
}

function logout() {
    window.location.replace("dashboard_login.html");
}

function describeTable(table) {
    window.location.replace("describe?table=" + table);
}

star_form.submit(submitStarForm);
movie_form.submit(submitMovieForm);