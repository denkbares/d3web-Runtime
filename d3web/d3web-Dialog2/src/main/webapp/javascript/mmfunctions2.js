var hidemenu;
var currentPM;

var ie = document.all && navigator.userAgent.indexOf("Opera") == -1;
var ff = document.getElementById && !document.all;


function openQuestion(e, id) {
    showMenu(e, 'pm_' + id);
}



function setAns(questionElementID, answerID) {
    var elements = document.dialogForm.elements[questionElementID];
    if (elements) {
		var index = elements.length;
		for(i=0; i<index; i++) {
			if(elements[i].value == answerID) {
				elements[i].checked = true;
          	}
      	}
    }
    doSubmit(); 
}



// PopupMenu
function showMenu(e, popupMenuVar) {  
    if (currentPM != null) {
    	currentPM.style.visibility = "hidden";
    }
    pm = document.getElementById(popupMenuVar);
    
    Element.hide(pm);
    
    var eventX = (ie ? event.clientX : ff ? e.clientX : e.x)-0;
	var eventY = ie ? event.clientY : ff ? e.clientY : e.y;
    
    pm.style.left = ie ? document.documentElement.scrollLeft + eventX + 'px' : ff ? window.pageXOffset + eventX + 'px' : eventX;
	pm.style.top = ie ? document.documentElement.scrollTop + event.clientY-2 + 'px' : ff ? window.pageYOffset + eventY + 'px' : eventY;
    
    currentPM = pm;
    pm.style.visibility = "visible";
    pm.className = "popupmenu opened";
    new Effect.Appear(pm, { duration: 0.2 });
}


function disableHide() {
	hidemenu = 1;
}

function enableHide() {
	hidemenu = 0;
}




function hideMenu() {
    if (currentPM != null) {
	    currentPM.className = "popupmenu";
	    currentPM.style.visibility = "hidden";
	    hidemenu = 0;
    }
    ////new Effect.Fade(pm);
}

function mouseUpAction(e) {
    if (hidemenu != 1) { 
    	hideMenu(); 
    }
}

function keyPressAction(e) {
    if (e.keyCode == 27) { 
    	hideMenu(); 
    } 
}
