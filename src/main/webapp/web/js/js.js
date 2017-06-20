/***
 * 求计算menu的高度
 */
$(function () {
    var window_width = $(window).width();
    var window_height = $(window).height();
    $(".menu, .content").height(window_height - 50+"px");
    $(".content").css("overflow-y","auto");
});

$(".introduce img").click(function () {
    $("#bg").show();
    $(".bookdetails").show();
});

$("#addbook").click(function () {
    $("#bg").show();
    $(".addbook").show();
});

$("#addmember").click(function () {
    $("#bg").show();
    $(".addmember").show();
});

$(".recode").click(function () {
    $("#bg").show();
    $(".readrecode").show();
});

$(".bookcontent").click(function () {
    $("#bg").show();
    $(".bookdetails").show();
});

$("#addrecodeok").click(function () {
    var identity = $.trim($("#identity").val());
    var name = $.trim($("#name").val());
    var phone = $.trim($("#phone").val());
    if(identity == ""){
        alert("请输入身份！");
        return false;
    }else if(name == ""){
        alert("请输入名字！");
        return false;
    }else if(phone == ""){
        alert("请输入手机号！");
        return false;
    }else{
        hidelayer();
        return true;
    }
});

$(".addbookok").click(function () {
    var bookname = $("#bookname").val();
    var bookisbn = $("#bookisbn").val();
    var bookanthor = $("#bookanthor").val();
    var bookpress = $("#bookpress").val();
    var bookpride = $("#bookpride").val();
    var bookpurchaser = $("#bookpurchaser").val();
    var addbooktime = $("#addbooktime").val();
    if(bookname == "" || bookisbn == "" || bookanthor == "" || bookpress =="" || bookpride == "" || bookpurchaser == "" || addbooktime == ""){
        alert("书籍信息不能为空！");
    }else{
        hidelayer();
        return true;
    }
});
function hidelayer() {
    $("#bg").hide();
    $(".dialog").hide();
}
