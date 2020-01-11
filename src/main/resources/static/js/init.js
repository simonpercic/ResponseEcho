
function renderjson(id, data) {

    $(id).jsonViewer(JSON.parse(data),{
        collapsed:true,
        rootCollapsable:false,
    });
}

function copyToClipboard(myData){
    navigator.clipboard.writeText(myData.toString());
}