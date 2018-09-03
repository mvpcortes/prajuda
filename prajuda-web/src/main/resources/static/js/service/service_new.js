
( function(){

    var SANITIZE_NAME_REGEX = new RegExp("([\\W_])", 'g')

    function usingNamePathDefault(){
        return $("#namePath").attr("disabled") !== undefined
    }

    function sanitizeName(realName){
        return realName.replace(SANITIZE_NAME_REGEX, "_").toLowerCase()
    }

    $("#namePath_change").click(function(event){
        if(usingNamePathDefault()){
            var el =$("#namePath")
                el.removeClass("is-disabled")
                $("#namePath_change").text("Use default")
                el.removeAttr("disabled")
        }else{
            var el =$("#namePath")
                el.addClass("is-disabled")
                $("#namePath_change").text("Edit")
                el.attr("disabled", "1")

            $("#namePath").val(sanitizeName($("#name").val()))
        }
    })

    $("#name").keyup(function(){
        if(usingNamePathDefault()){
            $("#namePath").val(sanitizeName($("#name").val()))
        }
    })

     $("#namePath").keyup(function(){
        $(this).val(sanitizeName($(this).val()))
    })
}())