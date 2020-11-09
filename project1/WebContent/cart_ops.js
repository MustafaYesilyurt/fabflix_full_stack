function successfulChange() {
    alert("Edit successful.")
}

function failedChange() {
    alert("Edit failed.")
}

function incrementQuantity(id, title) {
    $.ajax({
        url: 'api/edit-item',
        method: "POST",
        data: {'id':id, 'title':title, 'op':1},
        success: successfulChange,
        error: failedChange
    });
}

function decrementQuantity(id, title) {
    $.ajax({
        url: 'api/edit-item',
        method: "POST",
        data: {'id':id, 'title':title, 'op':2},
        success: successfulChange,
        error: failedChange
    });
}

function deleteItem(id, title) {
    $.ajax({
        url: 'api/edit-item',
        method: "POST",
        data: {'id':id, 'title':title, 'op':0},
        success: successfulChange,
        error: failedChange
    });
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

