A4J.AJAX.onError = function(req, status, message) {
	// redirect to errorpage
	window.location.href = "error.jsp";
};

function doSubmit() {
	document.getElementById("dialogForm:questions:send_hidden").click();
}

function saveLastClickedAnswer(answerId, caseId) {
	try {
		request = window.XMLHttpRequest ? new XMLHttpRequest()
				: new ActiveXObject("Microsoft.XMLHTTP");
		// kwiki_xmlhttp.onreadystatechange = gotSolutions;
		var jspURL = "JavaCommunicator.jsp?answerId=" + answerId + "&caseId="
				+ caseId;
		request.open("GET", jspURL);
		request.send(null);
	} catch (e) {
		alert(e);
	}
}

document.onmousemove = updateWMTT;

function updateWMTT(e) {
	mouseX = (document.all) ? window.event.clientX + getScrollLeft() : e.pageX;
	mouseY = (document.all) ? window.event.clientY + getScrollTop() : e.pageY;
}

function saveClickPosition() {
	mouseClickedOnButtonX = mouseX;
	mouseClickedOnButtonY = mouseY;
	var x = 2;
}

function getScrollTop() {
	var ScrollTop = document.body.scrollTop;
	if (ScrollTop == 0) {
		if (window.pageYOffset)
			ScrollTop = window.pageYOffset;
		else
			ScrollTop = (document.body.parentElement) ? document.body.parentElement.scrollTop
					: 0;
	}
	return ScrollTop;
}

function getScrollLeft() {
	var ScrollLeft = document.body.scrollLeft;
	if (ScrollLeft == 0) {
		if (window.pageXOffset)
			ScrollLeft = window.pageXOffset;
		else
			ScrollLeft = (document.body.parentElement) ? document.body.parentElement.scrollLeft
					: 0;
	}
	return ScrollLeft;
}

function setPopupPosition() {
	var d = document;
	var rootElm = (d.documentelement && d.compatMode == 'CSS1Compat') ? d.documentelement
			: d.body;
	var vpw = self.innerWidth ? self.innerWidth : rootElm.clientWidth; // viewport
	var questionPopup = document.getElementById("questionPopup");
	if (questionPopup != null) {
		questionPopup.style.top = mouseClickedOnButtonY + "px";
//		questionPopup.style.left = mouseClickedOnButtonX + "px";
		questionPopup.style.left = ((vpw - 500) / 2) + 'px';  
	}
}

function initExpPopup(diagId, text1, text2, text3) {
	var thePopup = '<div id="popmenu"><ul>';
	if (text1.length > 0) {
		thePopup += '<li><a id="reason_'
				+ diagId
				+ '" href="#" onclick="openDiagPopup(\'explanation.jsf?expl=explainReason&diag='
				+ diagId + '\', \'' + diagId + '\'); return false;">' + text1
				+ '</a></li>';
	}
	if (text2.length > 0) {
		thePopup += '<li><a id="concrete_'
				+ diagId
				+ '"href="#" onclick="openDiagPopup(\'explanation.jsf?expl=explainConcreteDerivation&diag='
				+ diagId + '\', \'' + diagId + '\'); return false;">' + text2
				+ '</a></li>';
	}
	if (text3.length > 0) {
		thePopup += '<li><a id="deriv_'
				+ diagId
				+ '"href="#" onclick="openDiagPopup(\'explanation.jsf?expl=explainDerivation&diag='
				+ diagId + '\', \'' + diagId + '\'); return false;">' + text3
				+ '</a></li>';
	}
	thePopup += '</ul></div>';
	return thePopup;
}

function removeValueFromTextfield(elements) {
	if (elements) {
		var index = elements.length;
		for (i = 0; i < index - 1; ++i) {
			elements[i].value = '';
		}
	}
}

function disableTF(elements) {
	if (elements) {
		for (i = 0; i < elements.length - 1; ++i) {
			elements[i].className = "textfieldDisabled";
			elements[i].value = "";
		}
	}
}

function enableTF(elements) {
	elements.className = "textfieldEnabled";
}

function checkMCConstraints(thisCheckbox, elements, ids) {
	if (thisCheckbox.checked) {
		// checkbox was selected
		var lists = ids.split("|");
		for ( var i = 0; i < lists.length; i++) {
			var oneList = lists[i];
			var oneIDArray = oneList.split(";");
			for ( var j = 0; j < oneIDArray.length; j++) {
				var oneID = oneIDArray[j];
				// disable this id if the list has only one entry
				if (oneIDArray.length == 1) {
					for ( var k = 0; k < elements.length; k++) {
						if (elements[k].value == oneID) {
							elements[k].disabled = true;
							break;
						}
					}
				}
				// check if this ID has to be disabled (if all other ids in the
				// list are checked
				else {
					var allOthersChecked = true;
					for ( var a = 0; a < oneIDArray.length; a++) {
						var IDtoCheck = oneIDArray[a];
						if (IDtoCheck != oneID) {
							for ( var k = 0; k < elements.length; k++) {
								if (elements[k].value == IDtoCheck
										&& elements[k].checked == false) {
									allOthersChecked = false;
									break;
								}
							}
						}
					}
					if (allOthersChecked) {
						for ( var k = 0; k < elements.length; k++) {
							if (elements[k].value == oneID) {
								elements[k].disabled = true;
								break;
							}
						}
					}
				}
			}
		}
	} else {
		// checkbox was deselected
		var lists = ids.split("|");
		for ( var i = 0; i < lists.length; i++) {
			var oneList = lists[i];
			var oneIDArray = oneList.split(";");
			for ( var j = 0; j < oneIDArray.length; j++) {
				var oneID = oneIDArray[j];
				// enable this id
				for ( var k = 0; k < elements.length; k++) {
					if (elements[k].value == oneID) {
						elements[k].disabled = false;
						break;
					}
				}
			}
		}
	}

}

function deselectUnknownAnd(elements, name) {
	if (elements) {
		for (i = 0; i < elements.length; i++) {
			if ((elements[i].value == 'MaU') || (elements[i].value == name)) {
				elements[i].checked = false;
			}
		}
	}
}

function deselectOptionsUnknownAnd(elements, name) {
	if (elements) {
		for (i = 0; i < elements.length; i++) {
			if ((elements[i].value == 'MaU') || (elements[i].value == name)) {
				elements[i].selected = false;
			}
		}
	}
}

function deselectAllBut(elements, name) {
	if (elements) {
		for (i = 0; i < elements.length; i++) {
			if (elements[i].value != name) {
				elements[i].checked = false;
			}
		}
	}
}

function deselectAllOptionsBut(elements, name) {
	if (elements) {
		for (i = 0; i < elements.length; i++) {
			if (elements[i].value != name && elements[i].selected == true) {
				elements[i].selected = false;
			}
		}
	}
}

function toggleUserSelectedDiags(diagtype) {
	var elements = document.forms['dialogForm'].elements[diagtype];
	var index = elements.length;
	if (index > 0) {
		for (i = 0; i < index; i++) {
			elements[i].checked = true;
		}
	} else {
		elements.checked = true;
	}
}

// Changes the cursor to an hourglass
function cursor_wait() {
	document.body.style.cursor = 'wait';
}

// Returns the cursor to the default pointer
function cursor_clear() {
	document.body.style.cursor = 'default';
}

function setUnknownHiddenfield() {
	document.forms['dialogForm'].elements['setunknownhiddenfield'].value = 'true';
}

function setClickedQASet(id) {
	document.forms['dialogForm'].elements['dialogForm:clickedQASet'].value = id;
}

function openDiagPopup(site, popupid) {
	popupwin = window
			.open(
					site,
					"diag_" + popupid,
					"width=650,height=400,left=50,top=50,resizable=yes,scrollbars=yes,dependent=yes");
	popupwin.focus();
}

function openSCM(diagId) {
	var site = 'scm.jsf?diagID=' + diagId;
	// um das Fenster eindeutig zu identifizieren
	e1 = window
			.open(
					site,
					"scm_" + diagId,
					"width=600,height=400,left=50,top=50,resizable=yes,scrollbars=yes,dependent=yes");
	e1.focus();
}

function openMMInfoPopup(mminfoId) {
	var site = 'mminfo.jsf?mminfo=all&mminfoid=' + mminfoId;
	popupwin = window
			.open(
					site,
					"mminfo_" + mminfoId,
					"width=650,height=400,left=50,top=50,resizable=yes,scrollbars=yes,dependent=yes");
	popupwin.focus();
}

function openCCPopup() {
	var site = 'comparecase.jsf?comptype=new';
	popupwin = window
			.open(
					site,
					'cc',
					"width=650,height=600,left=50,top=50,resizable=yes,scrollbars=yes,dependent=yes");
	popupwin.focus();
}

function openFreqPopup() {
	var site = 'answerFrequentness.jsf';
	popupwin = window
			.open(
					site,
					'freq',
					"width=750,height=650,left=50,top=50,resizable=yes,scrollbars=yes,dependent=yes");
	popupwin.focus();
}

function answerOnClick() {
	document.getElementById("dialogForm:questions:send").click();
}

function setAnswer(ansID) {
	var ans = document.getElementById(ansID);
	ans.click();
}

function makeInvisible(id) {
	var el = document.getElementById(id);
	if (el) {
		el.className = "invis";
	}
}

function makeVisible(id) {
	var el = document.getElementById(id);
	if (el) {
		el.className = "visible";
	}
}

function startCBRContainer(id) {
	document.forms['compareCaseForm'].elements['compareCaseForm:compType'].value = 'container';
	document.forms['compareCaseForm'].elements['compareCaseForm:compID'].value = id;
}

function startCBRDetailled(id) {
	document.forms['compareCaseForm'].elements['compareCaseForm:compType'].value = 'detailled';
	document.forms['compareCaseForm'].elements['compareCaseForm:compID'].value = id;
}

function startCBRSimple() {
	document.forms['compareCaseForm'].elements['compareCaseForm:compType'].value = 'simple';
	document.forms['compareCaseForm'].elements['compareCaseForm:compID'].value = '0';
}
