
( function(){
    $("#repositoryInfo_password_button").click(function(event){
        if($("#repositoryInfo_password").attr("type") == "password"){
            $("#repositoryInfo_password").attr("type", "text")
            $("#repositoryInfo_password_button").text("Hide")
        }else{
            $("#repositoryInfo_password").attr("type", "password")
            $("#repositoryInfo_password_button").text("Show")
        }
    })
}())