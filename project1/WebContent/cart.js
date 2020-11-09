let addcart = $(".addcart");

function handleAddToCartSuccess() {
    console.log("successfully added to cart");
    alert("Successfully added item to cart");
}

function handleAddToCartFailure() {
    console.log("failed to add to cart");
    alert("Failed to add to cart");
}

// function addToCart(title, price) {
//     //window.location.replace("api/add-to-cart?title=" + title + "&price=" + price);
//     $.ajax("api/add-to-cart?title=" + title + "&price=" + price, {
//         method: "GET",
//         success: handleAddToCartSuccess,
//         error: handleAddToCartFailure
//     });
// }

function addToCart(cartEvent) {
    console.log("attempting to add to cart");
    cartEvent.preventDefault();
    $.ajax({
             url: 'api/add-to-cart',
             method: "POST",
             data: addcart.serialize(),
             success: handleAddToCartSuccess,
             error: handleAddToCartFailure
        }
    );
}

function addToCart2(id, title, price) {
    console.log("attempting to add to cart2");
    $.ajax({
            url: 'api/add-to-cart',
            method: "POST",
            data: {'id':id, 'title':title, 'price':price},
            //data: JSON.stringify({'title':title, 'price':price}),
            success: handleAddToCartSuccess,
            error: handleAddToCartFailure
        }
    );
}

addcart.submit(addToCart);