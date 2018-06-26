scDefine([
	"scbase/loader!dojo/_base/lang",	
	"scbase/loader!wsc",
	"scbase/loader!sc/plat/dojo/Userprefs",
	"scbase/loader!sc/plat/dojo/utils/BaseUtils",
	"scbase/loader!sc/plat/dojo/utils/ScreenUtils",
	"scbase/loader!sc/plat/dojo/utils/ModelUtils",	
	"scbase/loader!sc/plat/dojo/utils/EditorUtils",
	"scbase/loader!sc/plat/dojo/utils/WizardUtils",
	"scbase/loader!wsc/components/shipment/backroompick/utils/BackroomPickUpUtils"
	],
	function(dLang,wsc,scUserprefs,scBaseUtils,scScreenUtils,scModelUtils,scEditorUtils,scWizardUtils,wscBackroomPickUtils){
		var searchUtils = dLang.getObject("components.shipment.search.utils.SearchUtils", true,wsc);
		
		searchUtils.scrollCenter = function(){
			var h = window.innerHeight;
			var gotoh = 600;
			if ( (h * 2 / 3) > gotoh )
				gotoh = h * 2 / 3;
			window.scroll( 0, gotoh );
		};
		
		searchUtils.handlePickTicket = function(event, bEvent, ctrl, args){
			var editor = scEditorUtils.getCurrentEditor();
			var wizard = scEditorUtils.getScreenInstance(editor);
			var wInstance = wizard.getWizardElemForId(wizard.wizardId).wizardInstance;
			var summaryScreen = scWizardUtils.getCurrentPage(wInstance);
			var isPrintServiceConfigured = false;
            /* Leave commented out until a 9.4 print utility is created
            isPrintServiceConfigured = wscPrintUtils.isPrintServiceConfigured(
            summaryScreen, "BackroomPick");
            			
            if (!(
            scBaseUtils.isBooleanTrue(
            isPrintServiceConfigured))) {
                wscPrintUtils.openPrintServiceConfigErrorBox(
                summaryScreen, "BackroomPick");
                return;
            } 
            */ 
            // gets the details based on whether we are coming from the shipmentSearch screen or elsewhere
            var shipmentDetails;
            if ( summaryScreen.id === "wsc_shipment_search_ShipmentSearch_0" ) {
            	shipmentDetails = summaryScreen.getModel("getShipmentList_output");
            } else {
            	shipmentDetails = summaryScreen.getModel("getShipmentDetails_output");
            }
			var status = scModelUtils.getStringValueFromPath("Shipment.Status.Status",shipmentDetails);
			if(!scBaseUtils.isVoid(status) && (status.indexOf("1100.70.06.10" ) != -1 || status.indexOf("1100.70.06.20" ) != -1)) {
				// only these status are valid to move further
				var modelOutput = scModelUtils.createNewModelObjectWithRootKey("Shipments");
				modelOutput.Shipments.Shipment=[];
				modelOutput.Shipments.Shipment[0]={};
				modelOutput.Shipments.Shipment[0].ShipmentKey=scModelUtils.getStringValueFromPath("Shipment.ShipmentKey",shipmentDetails);
				var printModel = null;
                printModel = wscBackroomPickUtils.createMultiApiInputForPrintService(
                summaryScreen, modelOutput);
                /* Leave commented out until a 9.4 print utility is created
                wscPrintUtils.openPrintPopupByPickType(
                summaryScreen, printModel, "BackroomPick");
				*/
			}else{
				var warningString = null;
                warningString = scScreenUtils.getString(
                summaryScreen, "NoPickTicketsToPrint");
				var textObj = null;
                textObj = scBaseUtils.getNewBeanInstance();
                var textOK = null;
                textOK = scScreenUtils.getString(
                summaryScreen, "Ok");
                scBaseUtils.addStringValueToBean("OK", textOK, textObj);
				scScreenUtils.showErrorMessageBox(
                summaryScreen, warningString, "waringCallback", textObj, null);
                return;
			}
		};
		
		
		searchUtils.getShipmentStatusDesc = function(descriptionField,dataValue,screenObject,widgetObject){
			if(!scBaseUtils.isVoid(descriptionField)){
				return scScreenUtils.getFormattedString(screenObject, descriptionField, null);
			}
		};
		
		searchUtils.getStatusModel = function( pickShipOrBoth ) {
			if ( pickShipOrBoth === " " || pickShipOrBoth === "" ) {
				var statusModel = { "StatusList" :
					{ "Status" : 
						[{"StatusCode":"1100.70.06.10", "StatusKey":"ReadyForBackroomPickup"},
						 {"StatusCode":"1100.70.06.20", "StatusKey":"PicksInProgress"},
						 {"StatusCode":"1100.70.06.30", "StatusKey":"ReadyForCustomerPickup"},
						 {"StatusCode":"1100.70.06.50", "StatusKey":"ReadyForPacking"},
						 {"StatusCode":"1100.70.06.70", "StatusKey":"PackingInProgress"},
						 {"StatusCode":"1300", "StatusKey":"Packed"},
						 {"StatusCode":"1400", "StatusKey":"ShippedPicked"}
						 ] 
					}
				}
				return statusModel;
			} else if ( pickShipOrBoth === "SHP" ) {
				var statusModel = { "StatusList" :
				{ "Status" : 
					[{"StatusCode":"1100.70.06.10", "StatusKey":"ReadyForBackroomPickup"},
					 {"StatusCode":"1100.70.06.20", "StatusKey":"PicksInProgress"},
					 {"StatusCode":"1100.70.06.50", "StatusKey":"ReadyForPacking"},
					 {"StatusCode":"1100.70.06.70", "StatusKey":"PackingInProgress"},
					 {"StatusCode":"1300", "StatusKey":"Packed"},
					 {"StatusCode":"1400", "StatusKey":"Shipped"}
					 ] 
				}
			}
			return statusModel;
			} else if ( pickShipOrBoth === "PICK" ) {
				var statusModel = { "StatusList" :
				{ "Status" : 
					[{"StatusCode":"1100.70.06.10", "StatusKey":"ReadyForBackroomPickup"},
					 {"StatusCode":"1100.70.06.20", "StatusKey":"PicksInProgress"},
					 {"StatusCode":"1100.70.06.30", "StatusKey":"ReadyForCustomerPickup"},
					 {"StatusCode":"1400", "StatusKey":"ShipmentComplete"}
					 ] 
				}
			}
			return statusModel;
			}			
		};
		
		return searchUtils;
});