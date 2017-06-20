var members  = ["liubm","xinghl","liuzh"];
$(".button").click(function () {
    var name = $.trim($(".name").val());
    if(name == ""){
        alert("名字不能为空！");
        return false;
    }else{
        var isLogin = members.contains(name); //返回true
        if(isLogin == true){
            window.setTimeout("reloadpage();",1000);
        }else{
            alert("用户名字错误，请检查后尝试！");
            return false;
        }
    }
});

function reloadpage(){
    window.location.href="main.html";
}