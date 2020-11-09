let payment_info = $("#payment_info");

/**
 * Handle the data returned by LoginServlet
 * @param resultDataString jsonObject
 */
function handlePaymentResult(resultDataString) {
    let resultDataJson = JSON.parse(resultDataString);

    console.log("handle payment response");
    console.log(resultDataJson);
    console.log(resultDataJson["status"]);

    // If login succeeds, it will redirect the user to index.html
    if (resultDataJson["status"] === "success") {
        window.location.replace("complete"); //.html
    } else {
        // If login fails, the web page will display
        // error messages on <div> with id "login_error_message"
        console.log("show error message");
        console.log(resultDataJson["message"]);
        $("#payment_error_message").text(resultDataJson["message"]);
    }
}

function submitPaymentForm(formSubmitEvent) {
    console.log("submit payment form");

    formSubmitEvent.preventDefault();

    $.ajax(
        "api/payment", {
            method: "POST",
            data: payment_info.serialize(),
            success: handlePaymentResult
        }
    );
}

function handlePriceMessage(resultDataString) {
    let resultDataJson = JSON.parse(resultDataString);

    console.log("handle price message");
    console.log(resultDataJson);
    console.log(resultDataJson["total_price"]);

    // show the session information
    $("#total_price").text("Total price: $" + resultDataJson["total_price"] + ".00");
}

$.ajax("api/payment", {
    method: "GET",
    success: handlePriceMessage
});

// Bind the submit action of the form to a handler function
payment_info.submit(submitPaymentForm);

