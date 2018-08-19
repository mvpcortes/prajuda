/**
    main JS for prajuda
*/


 /**
 Implement return behavior on return buttons
 */
 $("a.return , button.return , input[type='button'].return").click(function(e){
    e.preventDefault()
    history.back()
 })


/**
Implement button behavior to show/hide menu
*/
$(document).ready(function() {
    // Check for click events on the navbar burger icon
    $(".navbar-burger").click(function() {
        // Toggle the "is-active" class on both the "navbar-burger" and the "navbar-menu"
        $(".navbar-burger").toggleClass("is-active");
        $(".navbar-menu").toggleClass("is-active");
  
    });
  });