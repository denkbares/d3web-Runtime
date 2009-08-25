function go(url) {
parent.location=url;
}

function explainInWindow(renderer,servletclass) {

    var lok = servletclass + "?renderer=" +renderer +"&explain=" + document.form.explain.value;
  
    var url = window.document.URL;
    var i = url.indexOf("servlet/");
    if (i == -1) url = "";
    else url = url.substring(0,i);
    url = normalize(url);

// um das Fenster eindeutig zu identifizieren: Erklärungstyp, zu erklärendes Objekt, erster Teil der URL (bis "servlet/")
    e1 = window.open(lok,"explain"+renderer+document.form.explain.value+url,"width=600,height=350,left=50,top=50,resizable=yes,scrollbars=yes,dependent=yes");
    e1.focus();
   // e1.location.reload();

}

function explainInMainFrame(renderer,servletclass) {

    var lok = servletclass + "?renderer=" +renderer +"&explain=" + document.form.explain.value;
    top.center.document.location = lok;

}

function setExplainObj(objectID) {
document.form.explain.value = objectID;
}



function doNothing() {
}

function normalize(string) {
    var newStr = string;
    newStr = replace(newStr,".","P");
    newStr = replace(newStr,":","D");
    newStr = replace(newStr,"/","S");
    newStr = replace(newStr,"-","M");
    newStr = replace(newStr,"_","U");
    newStr = replace(newStr,"^","D");
    newStr = replace(newStr,"+","P");
    newStr = replace(newStr,"*","M");
    newStr = replace(newStr,"~","T");
    newStr = replace(newStr,",","K");
    newStr = replace(newStr,";","S");
    newStr = replace(newStr,"|","O");
    newStr = replace(newStr,"@","A");
    newStr = replace(newStr,"'","A");
    return newStr;
}

function replace(string,text,by) {
// Replaces text with by in string
    var strLength = string.length, txtLength = text.length;
    if ((strLength == 0) || (txtLength == 0)) return string;

    var i = string.indexOf(text);
    if ((!i) && (text != string.substring(0,txtLength))) return string;
    if (i == -1) return string;

    var newstr = string.substring(0,i) + by;

    if (i+txtLength < strLength)
        newstr += replace(string.substring(i+txtLength,strLength),text,by);

    return newstr;
}

