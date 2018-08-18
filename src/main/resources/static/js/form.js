/**
Define string supplant method to implement string template
@see https://pt.stackoverflow.com/a/78752/94219
*/
String.prototype.templateTo = function (o) {
    return this.replace(/\${([^{}]*)}/g,
        function (a, b) {
            var r = o[b];
            return typeof r === 'string' || typeof r === 'number' ? r : a;
        }
    );
};
/**
    Define de modal loading
  */

var modalLoading = {
    mapRegistered   : new Set(),
    getModal        : function(){
        var modal = $("#"+this.ID_TAG);
        return (modal.length > 0)?modal:this.createModal()
     },
    createModal     : function(){
                        $("body").append(
                            $("<div/>").addClass("modal").attr("id", this.ID_TAG).append([
                                $("<div/>").addClass("modal-background"),
                                    $("<img/>").addClass("rotating-image").attr("id", "modal-loading-image").attr("src", '/images/loading.png')
                            ])
                        )
                        return $("#"+this.ID_TAG)
                      },
    show            : function(applicant){
                        if(this.mapRegistered.size == 0){
                            this.getModal().addClass("is-active")
                        }
                        this.mapRegistered.add(applicant)
                      },
    hide            : function(applicant){
                        this.mapRegistered.delete(applicant)
                        if(this.mapRegistered.size <= 0){
                            this.getModal().removeClass("is-active")
                        }
                      },
    ID_TAG          : "modal-loading"
    }

/**
Implement a form submitted by ajax and deal with errors.
*/
$("form.ajax-form").submit(function(e){
    console.log("XUXU XAXA XEXE")

    var frm = $(this);

    var getRedirect = function(myFrm, myData){
        if(frm.attr("data-redirect") != null){
            return frm.attr("data-redirect").templateTo(myData)
        }else if(myData.redirect && myData.redirect != null){
            return myData.redirect
        }else{
            throw "Cannot found redirect url"
        }
    }

    var showError = function(errorMessages){
        var oldErrorMessages = frm.find("[data-error-for-showed]").hide()
            oldErrorMessages.addClass("ajax-form-error")
            oldErrorMessages.removeClass("ajax-form-error-showed")
            oldErrorMessages.text("")//hide and erase before errors

        for (var property in errorMessages) {
            if (errorMessages.hasOwnProperty(property)) {
                var em = errorMessages[property]
                var errorMessage = frm.find("[data-error-for='${field}']".templateTo(em))
                console.log("[data-error-for='${field}']".templateTo(em))
                console.log(frm.find("[data-error-for='${field}']".templateTo(em)).length + "!!!" + em.message)
                console.log("ERRR: "+errorMessage.attr("id"))
                errorMessage.show()
                errorMessage.removeClass("ajax-form-error")
                errorMessage.addClass("ajax-form-error-showed")
                errorMessage.text(em.message)
            }
        }

        console.log(frm.html())
    }

    $.ajax({
        type       :frm.attr("method"),
        url        :frm.attr("action"),
        data       :frm.serialize(),
        beforeSend :function(){
            console.log("beforeSend::show")
            modalLoading.show(frm)
            }
        })
        .always(function(){
            console.log("always::xxxxxxxxx")
            modalLoading.hide(frm)
         })
        .done(function(data){
            window.location.href = getRedirect(frm, data)
        })
        .fail(function(xhr, strStatus, errorThrown){
            console.log("xpto:" + errorThrown);
            console.log("xpto:strStatus" + errorThrown);
            console.log("xpto:xhr" + JSON.stringify(xhr.responseJSON));
            if(xhr.responseJSON && xhr.responseJSON !=null){
                showError(xhr.responseJSON)
            }else{
            }
        })

        e.preventDefault()
        return false
    })

//$(document).ready(function(){
//    $("body").append(
//        $("<div/>").addClass("modal").attr("id", "modal-ajax-form").append([
//            $("<div/>").addClass("modal-background"),
//            $("<div/>").addClass("modal-content").append(
//                $("<div/>").addClass("box").append(
//                    $("<img/>").attr("id", "modal-ajax-form-image").addClass("rotating_image").attr("src", '/images/loading.png')
//                )
//            )
//        ])
//    );
//})