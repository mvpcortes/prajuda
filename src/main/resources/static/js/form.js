
$("form#ajax-form").submit(function(e){
    e.preventResult();

    var frm = $(this);
    $.ajax({
        type       :frm.attr("method"),
        url        :frm.attr("action"),
        data       :frm.serialize(),
        intervalId : null,
        animationId: 0,
        beforeSend :function(){
            $(".modal-ajax-form").show(500);
                intervalId = setTimeout(function(){
                    $(".modal-ajax-form-image").animate("transform:rotate(" + animationId + "deg)");
                    animationId++;
                }, 50);
            }
        })
        .always(function(){
            $(".modal-ajax-form").hide(500);
            clearTimeOut(intervalId);
         })
        .done(function(data){
            if(data && data.redirect){
                $(window).attr("location",data.redirect);
            }else{
                console.log(data)
            }
        })
        .fail(function(xhr, strStatus, errorThrown){
            console.log(errorThrown);
        })
    });

$(document).ready(function(){
    $("body").append(
        $("<div/>").addClass("modal").attr("id", "modal-ajax-form").append([
            $("<div/>").addClass("modal-background"),
            $("<div/>").addClass("modal-content").append(
                $("<div/>").addClass("box").append(
                    $("<img/>").attr("id", "modal-ajax-form-image").attr("src", '/images/loading.png')
                )
            )
        ])
    );
})