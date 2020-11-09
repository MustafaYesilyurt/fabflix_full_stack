function handleCompleteMessage(resultDataString) {
    let resultDataJson = JSON.parse(resultDataString);

    console.log("handle complete message");
    console.log(resultDataJson);
    console.log(resultDataJson["order_info"]);

    // show the session information
    $("#order_info").text("Order Info: \n" + resultDataJson["order_info"]);
}

$.ajax("complete", {
    method: "GET",
    success: handleCompleteMessage
});