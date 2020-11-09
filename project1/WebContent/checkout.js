let checkout = $("#checkout");

function goToCart(goevent) {
    goevent.preventDefault();
    window.location.replace("shopping-cart");
}

checkout.submit(goToCart);