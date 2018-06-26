/*
 * Licensed Materials - Property of IBM
 * IBM Sterling Order Management Store (5725-D10)
 *(C) Copyright IBM Corp. 2014 , 2015 All Rights Reserved. , 2015 All Rights Reserved.
 * US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 */
scDefine([
	"scbase/loader!dojo/_base/lang",	
	"scbase/loader!wsc",
	"scbase/loader!dojo/_base/array",
	"scbase/loader!sc/plat/dojo/utils/BaseUtils",
	"scbase/loader!sc/plat/dojo/utils/BundleUtils",
	"scbase/loader!sc/plat/dojo/utils/ScreenUtils",
    "scbase/loader!ias/utils/UIUtils",
	"scbase/loader!ias/utils/ContextUtils",
	"scbase/loader!ias/utils/ScreenUtils",
	"scbase/loader!ias/utils/WizardUtils",
	"scbase/loader!sc/plat/dojo/utils/WidgetUtils",
	"scbase/loader!sc/plat/dojo/utils/Util",
	"scbase/loader!sc/plat/dojo/utils/EventUtils",
	"scbase/loader!sc/plat/dojo/Userprefs",
	"scbase/loader!ias/utils/BaseTemplateUtils",
	"scbase/loader!sc/plat/dojo/utils/ModelUtils",
	"scbase/loader!sc/plat/dojo/utils/EditorUtils",
	"scbase/loader!sc/plat/dojo/utils/WizardUtils",
	"scbase/loader!sc/plat/dojo/utils/RepeatingPanelUtils",	
	"scbase/loader!wsc/common/widgets/LabelSetWidget",
	"scbase/loader!sc/plat/dojo/utils/ControllerUtils",
	"scbase/loader!ias/utils/UOMUtils",
	"scbase/loader!wsc/components/common/utils/CommonUtils",
	"scbase/loader!sc/plat/dojo/utils/ResourcePermissionUtils",
	"scbase/loader!dojox/html/entities"
	],
	function(dLang,wsc,dArray,scBaseUtils,scBundleUtils,scScreenUtils,iasUIUtils,iasContextUtils,iasScreenUtils,iasWizardUtils,scWidgetUtils,scUtil,scEventUtils,scUserprefs,iasBaseTemplateUtils,scModelUtils,scEditorUtils,scWizardUtils,scRepeatingPanelUtils,wscLabelSetWidget,scControllerUtils,iasUOMUtils,wscCommonUtils,scResourcePermissionUtils,dHtmlEntities){
		var util = dLang.getObject("components.shipment.common.utils.ShipmentUtils", true,wsc);
		
		util.IsQuantityValueChanged = function (screen,sourceNamespace, sourcePath, targetNamespace, targetPath) {
			
			var isValueChanged = false;
			var originalQtyTmp = scModelUtils.getNumberValueFromPath(sourcePath, scScreenUtils.getModel(screen, sourceNamespace, null));
			if(!iasUIUtils.isValueNumber(originalQtyTmp)){
				originalQtyTmp = 0;
			}
			var currQty = scModelUtils.getNumberValueFromPath(targetPath, scScreenUtils.getTargetModel(screen, targetNamespace, null));			
			if (iasUIUtils.isValueNumber(currQty) && !scBaseUtils.equals(Number(originalQtyTmp),currQty)) {
				isValueChanged = true;
			}

			return isValueChanged;
		};
		
		util.applyOverdueStyling = function(unformattedValue,screen,widgetId,namespace,shipment) {
			var dueInDate = "";
			if(!scBaseUtils.isVoid(shipment) && !scBaseUtils.isVoid(shipment.Shipment.TimeRemaining) && util.showSLA(shipment.Shipment.Status.Status)) {
				dueInDate=shipment.Shipment.TimeRemaining;
				/**
				 * 	var isOverdue = "";
				 *	isOverdue = shipment.Shipment.IsOverdue;
				 *	if(scBaseUtils.equals(isOverdue,"true")){
				 *		scWidgetUtils.addClass(screen, widgetId, "pastDue");
				 *    }
				 */
				
				/**
				 * Defect 445014: Due In Label will appear in colored text based on image file name. 
				 * CSS class are added based image file names.
				 */				
				var imageUrl =  shipment.Shipment.ImageUrl;
				var cssClass = "";
				if(!scBaseUtils.isVoid(imageUrl) && imageUrl.lastIndexOf("/") != -1) {
					cssClass = imageUrl.substring(imageUrl.lastIndexOf("/") + 1, imageUrl.indexOf("."));
					if(!scBaseUtils.isVoid(cssClass)) {
						scWidgetUtils.addClass(screen, widgetId, cssClass);
						//scWidgetUtils.addClass(screen,"lblShipmentDesc",cssClass);
					}
				}
			}
			return dueInDate;
			
		};
		
		
		util.createShipmentLines = function(screen, uIdContentPane,  parentUID, bindingData, modelData){
			
			var shipresultsPanel = screen.getWidgetByUId(uIdContentPane);
			if(shipresultsPanel)
				{
					scWidgetUtils.destroyWidget(screen,uIdContentPane);
				}
			var configParams_cp = {
			"CONTENTPANE": {
				"uId": uIdContentPane
				
			}
			};
			
			configParams_cp.CONTENTPANE.BindingData = {};
			configParams_cp.CONTENTPANE.BindingData.repeatingscreenGeneratorFunction=bindingData.BindingData.repeatingscreenGeneratorFunction;
			
			if(!scBaseUtils.isVoid(bindingData.BindingData.uniqueIdGeneratorFunc) && !(bindingData.BindingData.uniqueIdGeneratorFunc == undefined)) {
				configParams_cp.CONTENTPANE.BindingData.uniqueIdGeneratorFunc=bindingData.BindingData.uniqueIdGeneratorFunc;
			}
			
			configParams_cp.CONTENTPANE.BindingData.repeatingScreen={};
			configParams_cp.CONTENTPANE.BindingData.repeatingScreen.sourcenamespace=bindingData.BindingData.RepeatingScreenData.sourcenamespace;
			configParams_cp.CONTENTPANE.BindingData.repeatingScreen.repeatingscreenpath=bindingData.BindingData.RepeatingScreenData.repeatingscreenpath;
			configParams_cp.CONTENTPANE.BindingData.repeatingScreen.repeatingnamespace=bindingData.BindingData.RepeatingScreenData.repeatingnamespace;
			configParams_cp.CONTENTPANE.BindingData.repeatingScreen.path=bindingData.BindingData.RepeatingScreenData.path;
			
			var resultsPanel = scWidgetUtils.createWidgetInScreen("CONTENTPANE", screen, uIdContentPane, configParams_cp);
			scWidgetUtils.placeAt(screen, parentUID, resultsPanel, "first");
			scBaseUtils.setModel(screen,bindingData.BindingData.RepeatingScreenData.sourcenamespace,modelData,null);
		};

		
		util.openContainerPackwizard = function (event, bEvent, ctrl, args) {
		
			//var screen = scEditorUtils.getCurrentEditor();
			
			var currentEditor = scEditorUtils.getCurrentEditor();
			
			 var scr = scEditorUtils.getScreenInstance(currentEditor);
			 var wizInstance =scr.getWizardElemForId(scr.wizardId).wizardInstance;
			 var currentScreen = scWizardUtils.getCurrentPage(wizInstance);
				
             var taskInput = scScreenUtils.getInitialInputData(currentEditor);
			
             var mashupContext = scControllerUtils.getMashupContext(currentScreen);
             
             var apiInput = scBaseUtils.getNewModelInstance();
             var shipmentKey = scBaseUtils.getAttributeValue("Shipment.ShipmentKey",false,taskInput);
			  scBaseUtils.setAttributeValue("Shipment.ShipmentKey", shipmentKey, apiInput);
			  scBaseUtils.setAttributeValue("Shipment.Action", "Pack", apiInput);
			  scBaseUtils.setAttributeValue("Action", "Pack", mashupContext);
            /* scBaseUtils.setAttributeValue("Order.OrderNo", "Test26", apiInput);
             scBaseUtils.setAttributeValue("Order.Action", "Pack", apiInput);*/
             iasUIUtils.callApi(currentScreen, apiInput, "common_getShipmentDetails",mashupContext);
            // iasUIUtils.callApi(currentScreen, apiInput, "common_getShipmentListForOrder",mashupContext);
             
		};
		
		util.numberLessThanOrEqual = function(/*Number*/number1, /*Number*/number2){
			return number1 <= number2;
		};
		
		util.subtract = function(num1, num2) {
			var num = num1 - num2;
			return num;
		};
		
		util.openpickOrPackFlow = function(wizardArgs,contextParams){
			
		
			 var globalController = iasContextUtils.getglobalControllerScreenInstance();
			 var mashupContext = scControllerUtils.getMashupContext(globalController);
			 var shipmentKey = scBaseUtils.getAttributeValue("Shipment.ShipmentKey",false,wizardArgs[1]);
			 var apiInput = scBaseUtils.getNewModelInstance();
			 
			   scBaseUtils.setAttributeValue("Shipment.ShipmentKey", shipmentKey, apiInput);
	           scBaseUtils.setAttributeValue("contextParams", contextParams, mashupContext);
	           scBaseUtils.setAttributeValue("wizardArgs", wizardArgs, mashupContext);
	           iasUIUtils.callApi(globalController, apiInput, "common_getShipmentDetails",mashupContext);
	           
			
		};
								
		util.abandonedCallbackHandler= function(
		        actionPerformed, model, popupParams) {
		            if (!(scBaseUtils.isVoid(popupParams))) {
		                var shipmentModel = null;
		                shipmentModel = scModelUtils.getModelObjectFromPath("binding.Shipment", popupParams);
		                var wizardArgs =  scBaseUtils.getAttributeValue("binding.wizardArgs",false,popupParams);
		                var  WizardToopen=wizardArgs[0];
		                scModelUtils.setStringValueAtModelPath("Shipment.actionPerformed", actionPerformed, shipmentModel);
		                wizardArgs[1].Shipment.AssignedToUserId=shipmentModel.Shipment.AssignedToUserId;
						wizardArgs[1].Shipment.Status=shipmentModel.Shipment.Status;
		                if (scBaseUtils.equals("STARTOVER", actionPerformed) || scBaseUtils.equals("CONTINUE", actionPerformed)) {
		                 
			          iasUIUtils.openWizardInEditor(WizardToopen, wizardArgs[1], "wsc.desktop.editors.ShipmentEditor",popupParams.Screen);

		                }
		            }
        };
        
        util.openContainerPackwizardOnMashupCompletion = function(mashupContext,
				mashupRefObj, mashupRefList, inputData, hasError, data,
				screen){
        	
        	 if (!scBaseUtils.isEmptyArray(mashupRefList)) {
        		 
        		   var modelInput = null;
        		   var changeShipmentIscalled = false;
					for (var counter = 0; counter < scBaseUtils.getAttributeCount(mashupRefList); counter++) {
						 mashupRefBean = mashupRefList[counter];
						 var mashupInputBean = inputData[counter];
						 mashupRefId = scBaseUtils.getStringValueFromBean("mashupRefId", mashupRefBean);
						 modelInput = scBaseUtils.getModelValueFromBean("mashupInputObject", mashupInputBean);
						 if(scBaseUtils.equals(mashupRefId,"containerPack_changeShipment")){
							 changeShipmentIscalled = true;
							 break;
						 }
					}
					if(changeShipmentIscalled){
						iasUIUtils.openWizardInEditor("wsc.components.shipment.container.pack.ContainerPackWizard", modelInput, "wsc.desktop.editors.ShipmentEditor", screen);
					}
						
        	 }
        	 
        };
        
		util.getOfQty = function(unformattedValue,screen,widget,namespace,shipmentLine) {
		
			var dataArray = [];
			var uom="";
			var quantity=unformattedValue;
			var uomDisplayFormat="";
			
			uom = scModelUtils.getStringValueFromPath("ShipmentLine.OrderLine.ItemDetails.DisplayUnitOfMeasure", shipmentLine);
			uomDisplayFormat = scModelUtils.getStringValueFromPath("ShipmentLine.OrderLine.ItemDetails.UOMDisplayFormat", shipmentLine);
			
			if(scBaseUtils.isVoid(quantity)){
				quantity = scModelUtils.getStringValueFromPath("ShipmentLine.Quantity", shipmentLine);
			}		
			
			var shortageQty = scModelUtils.getStringValueFromPath("ShipmentLine.ShortageQty", shipmentLine);
			shortageQty = scBaseUtils.isVoid(shortageQty)?Number("0"):Number(shortageQty);
			var qty = scModelUtils.getStringValueFromPath("ShipmentLine.OriginalQuantity", shipmentLine); 
			if(!scBaseUtils.isVoid(screen.flowName) && scBaseUtils.equals(screen.flowName,"CustomerPick")) {
				qty = scModelUtils.getStringValueFromPath("ShipmentLine.Quantity", shipmentLine); 
			}
			
			displayQtyUOM =  iasUOMUtils.getFormatedQuantityWithUom(qty, uom, uomDisplayFormat);
			dataArray[0] =  displayQtyUOM;	
			
			return scScreenUtils.getFormattedString(screen,"OfQty",dataArray);		
				
		};
		
		util.getFormattedShortageQuantity = function(unformattedValue,screen,widget,namespace,shortageModel) {
			
			var dataArray = [];
			var uom="";
			var quantity=unformattedValue;
			var uomDisplayFormat="";
			var formattedUOM = "";

			var shipmentLineModel = scScreenUtils.getModel(screen,"ShipmentLine");

			uom = scModelUtils.getStringValueFromPath("ShipmentLine.OrderLine.ItemDetails.DisplayUnitOfMeasure", shipmentLineModel);
			uomDisplayFormat = scModelUtils.getStringValueFromPath("ShipmentLine.OrderLine.ItemDetails.UOMDisplayFormat", shipmentLineModel);

			var shortageQty = scModelUtils.getStringValueFromPath("ShipmentLine.ShortageQuantity", shortageModel);
			shortageQty = scBaseUtils.isVoid(shortageQty)?Number("0"):Number(shortageQty);
			uom="";
			displayQtyUOM =  iasUOMUtils.getFormatedQuantityWithUom(shortageQty, uom, uomDisplayFormat);

			return displayQtyUOM; 
			
				
		};	
		
		util.getFormattedDisplayQuantity = function(unformattedValue,screen,widget,namespace,shipmentLine,options) {
			var uom="";
			var quantity=unformattedValue;
			var uomDisplayFormat="";	
			uom = scModelUtils.getStringValueFromPath("ShipmentLine.OrderLine.ItemDetails.DisplayUnitOfMeasure", shipmentLine);
			uomDisplayFormat = scModelUtils.getStringValueFromPath("ShipmentLine.OrderLine.ItemDetails.UOMDisplayFormat", shipmentLine);
			quantity = scBaseUtils.isVoid(quantity)?Number("0"):Number(quantity);
			displayQtyUOM =  iasUOMUtils.getFormatedQuantityWithUom(quantity, uom, uomDisplayFormat);
			return displayQtyUOM;				
		};
		
		util.getFormattedOfDisplayQuantity = function(unformattedValue,screen,widget,namespace,shipmentLine) {
			var displayQtyUOM = util.getFormattedDisplayQuantity(unformattedValue,screen,widget,namespace,shipmentLine);
			return scScreenUtils.getFormattedString(screen,"OfQty",[displayQtyUOM]);				
		};
		
		util.showFormattedDisplayQuantityIfGreaterThanZero = function(unformattedValue,screen,widget,namespace,shipmentLine,options) {
			var quantity = scBaseUtils.isVoid(unformattedValue)?Number("0"):Number(unformattedValue);
			if(quantity>0){
				return util.getFormattedDisplayQuantity(unformattedValue,screen,widget,namespace,shipmentLine);				
			}
			else{
				scWidgetUtils.hideWidget(screen, widget, true);
				return unformattedValue;
			}
		};

        util.getInProgressWarningString = function(screen,shipmentModel,context){
			
			var dataArray = scBaseUtils.getNewArrayInstance();
			var assignToUserId = "";
			var deliveryMethod = ""; 
			var warningString = "";
			
			if(!scBaseUtils.isVoid(shipmentModel) && !scBaseUtils.isVoid(shipmentModel.Shipment.AssignedToUserId)) {
					assignToUserId = 	dHtmlEntities.encode(shipmentModel.Shipment.AssignedToUserId);		
					deliveryMethod =    shipmentModel.Shipment.DeliveryMethod;
					 
			} else {
				//console.log("ERROR: Unable to retrive AssignToUserId from Shipment");
			}
			//var context = scBaseUtils.getAttributeValue("context",false,contextParams);
			scBaseUtils.appendToArray(dataArray, assignToUserId);
			
			if(scBaseUtils.equals(context,"Pack")){
			
		     warningString =  scScreenUtils.getFormattedString(screen,"ContainerPackInProgress",dataArray);

			}else if (scBaseUtils.equals(context,"BackroomPick")){
				
				if (scBaseUtils.equals(deliveryMethod,"SHP")){
					
				     warningString =  scScreenUtils.getFormattedString(screen,"BackroomPickInProgressForShipping",dataArray);

				}else if (scBaseUtils.equals(deliveryMethod,"PICK")){
					
					warningString =  scScreenUtils.getFormattedString(screen,"BackroomPickInProgressForPickup",dataArray);
				}

			}
			
			
			return warningString;
			
		};
		
		util.startsWith = function(status,InprogressStaus){
			
			if ( typeof String.prototype.startsWith != 'function' ) {
				  String.prototype.startsWith = function( str ) {
				    return (this.substring( 0, str.length ) === str);
				  };
				  }
			return status.startsWith(InprogressStaus);
		};
		
	
		util.handleGetshipmentDetailsAPICall= function(screen, shipmentDetailsModel, mashupContext){
			

			if ( typeof String.prototype.startsWith != 'function' ) {
				  String.prototype.startsWith = function( str ) {
				    return (this.substring( 0, str.length ) === str);
				  };
			}
			
			var contextParams = scBaseUtils.getAttributeValue("contextParams",false,mashupContext);
            var wizardArgs =  scBaseUtils.getAttributeValue("wizardArgs",false,mashupContext);
            var callingScreen = wizardArgs[3];
			var isDiffUserCheckReqd = null;
			var context = scBaseUtils.getStringValueFromBean("context",contextParams);
			var assignedToDiffUser ="N";
			var isContextValid = false;
			var transactionArray = scModelUtils.getModelListFromPath("Shipment.AllowedTransactions.Transaction", shipmentDetailsModel);
			var len = scBaseUtils.getAttributeCount(transactionArray);
			
			if(scBaseUtils.isVoid(scBaseUtils.getStringValueFromBean("userValidationReqd",contextParams))){
				userValidationReqd="Y";
			}else{
				userValidationReqd =scBaseUtils.getStringValueFromBean("userValidationReqd",contextParams);
			}
			
			
			 if (scBaseUtils.greaterThan(len, 0)) {
				 
				 var transaction = null;
				 var tranid = null;
				 
				 for (var i=0; i<=len; i++)
			      {
					 transaction = scBaseUtils.getArrayBeanItemByIndex(transactionArray, i);
					 tranid = scBaseUtils.getAttributeValue("Tranid", false, transaction);
					// tranid ="PACK_SHIPMENT_COMPLETE";
					 if(scBaseUtils.equals(context,"BackroomPick")){
							if(tranid.startsWith("YCD_BACKROOM_PICK")){
								isContextValid = true;
								break;
							}
							
					 }
					 else if(scBaseUtils.equals(context,"Pack")){
						 
						 if(tranid.startsWith("PACK_SHIPMENT_COMPLETE")){
							 isContextValid = true;
							 break;
						 }
						 
					 }
					 else if(scBaseUtils.equals(context,"UnPack")){
						 
						 if(tranid.startsWith("UNDO_PACK_SHMT_COMPLETE")){
							 isContextValid = true;
							 break;
						 }
						
					 }
					 else if(scBaseUtils.equals(context,"CustomerPick")){
						 
						 if(tranid.startsWith("CONFIRM_SHIPMENT")){
							 isContextValid = true;
							 break;
						 }
						
					 }
					 
					 //TODO: Shipment is valid but not in a Valid status to open the requested wizard. open shipment summary and show error message there.
					 
					 //iasBaseTemplateUtils.showMessage(screen,scBundleUtils.getString("ShipmentContextError"), "error");
					 //shipmentDetailsModel.Shipment.showError ="Y";
					//iasUIUtils.openWizardInEditor("wsc.shipment.wizards.ShipmentSummaryWizard", shipmentDetailsModel, "wsc.desktop.editors.ShipmentEditor", screen);

			      }
				 
				 // put the logic here 
				if(!isContextValid){ 
				iasUIUtils.openWizardInEditor("wsc.shipment.wizards.ShipmentSummaryWizard", wizardArgs[1], "wsc.desktop.editors.ShipmentEditor", screen);
				}
				
			 }else{
				 
				 //TODO: Throw error
				 iasBaseTemplateUtils.showMessage(screen,scBundleUtils.getString("InvalidShipmentContext"), "error");
			 }
			
			
			 if(isContextValid){
				
				if(!scBaseUtils.isVoid(shipmentDetailsModel) && !scBaseUtils.isVoid(shipmentDetailsModel.Shipment.AssignedToUserId)) {
					if(!scBaseUtils.equals(shipmentDetailsModel.Shipment.AssignedToUserId,scUserprefs.getUserId())) {
						assignedToDiffUser= "Y";
					} 
				}
				
				if(scBaseUtils.equals(assignedToDiffUser,"N")||(scBaseUtils.equals(userValidationReqd,"N"))){
					shipmentDetailsModel.Shipment.assignedToDiffUser="N";
					wizardArgs[1].Shipment.AssignedToUserId=shipmentDetailsModel.Shipment.AssignedToUserId;
					wizardArgs[1].Shipment.Status=shipmentDetailsModel.Shipment.Status;
					iasUIUtils.openWizardInEditor(wizardArgs[0], wizardArgs[1], "wsc.desktop.editors.ShipmentEditor",callingScreen);
				}
				else{
					var popupParams = scBaseUtils.getNewBeanInstance();
					var bindings = scBaseUtils.getNewBeanInstance();
					popupParams.url = null;
					popupParams.Screen = callingScreen;
					popupParams.outputNamespace = "popupData";
					//shipmentDetailsModel.Shipment.WizardToopen=scBaseUtils.getStringValueFromBean("WizardToOpen",contextParams);
					//popupParams.Shipment = shipmentDetailsModel;

					//TODO addStringValueToBean to pass the context to call back handler
					
					scBaseUtils.addModelValueToBean("Shipment", shipmentDetailsModel, bindings);
					scBaseUtils.addModelValueToBean("contextParams", contextParams, bindings);
					scBaseUtils.addModelValueToBean("wizardArgs", wizardArgs, bindings);
					scBaseUtils.addBeanValueToBean("binding", bindings, popupParams);
					
					var classInfo = {};
					/*classInfo.className = "Util";
					classInfo.packageName="wsc.components.shipment.common.utils";*/
					
					var dialogParams = scBaseUtils.getNewBeanInstance();
					dialogParams.closeCallBackHandler = "abandonedCallbackHandler";
					dialogParams.classInfo= classInfo;
					dialogParams['class'] = "idxModalDialog";
		            
					iasUIUtils.openSimplePopup("wsc.components.shipment.common.screens.PickorPackWarningDialog", "Label_warning_message", callingScreen, popupParams, dialogParams);
				}
				
			}
			
		
			
		};
		
		util.handleBarcodeScan = function(screen,sourceNamespace,targetNamespace,mashupRefId,widgetUId){
			var barCodeModel = null;
            var barCodeData = null;
            if (!(scScreenUtils.isValid(screen, targetNamespace))) {
                var warningString = scScreenUtils.getString(screen, "Message_InvalidBarCodeData");               
                scScreenUtils.showErrorMessageBox(screen, warningString, "waringCallback", 
						iasUIUtils.getTextOkObjectForMessageBox(screen), null);
            } else {
                barCodeModel = scScreenUtils.getTargetModel(screen, targetNamespace, null);
                barCodeData = scModelUtils.getStringValueFromPath("BarCode.BarCodeData", barCodeModel);
                if(typeof String.prototype.trim !== 'function') {
 					 String.prototype.trim = function() {
   					 return this.replace(/^\s+|\s+$/g, ''); 
  					};
				}
                if (scBaseUtils.isVoid(barCodeData) || scBaseUtils.isVoid(barCodeData.trim())) {
					var warningString = scScreenUtils.getString(screen, "Message_InvalidBarCodeData");					
					scScreenUtils.showErrorMessageBox(screen, warningString, "waringCallback", 
							iasUIUtils.getTextOkObjectForMessageBox(screen), null);				
				} 
				else {
                    iasUIUtils.callApi(screen, barCodeModel, mashupRefId, null);
					scScreenUtils.setModel(screen, sourceNamespace, null, null);
					scWidgetUtils.setFocusOnWidgetUsingUid(screen, widgetUId);
                }
            }	
		};
		
		util.highlightLastScannedRepeatingPanel = function(screen,lastScannedPanelValueVar,repPanelUId,highlightCssClass){
			if(!scBaseUtils.isVoid(screen[lastScannedPanelValueVar]) 
				&& !scBaseUtils.equals(screen[lastScannedPanelValueVar],repPanelUId)){
				scWidgetUtils.removeClass(screen,screen[lastScannedPanelValueVar],highlightCssClass);
				if(!scBaseUtils.isVoid(screen.getWidgetByUId(repPanelUId))){
					scWidgetUtils.addClass(screen,repPanelUId,highlightCssClass);
				}				
			}
			else{
				if(!scBaseUtils.isVoid(screen.getWidgetByUId(repPanelUId))){
					scWidgetUtils.addClass(screen,repPanelUId,highlightCssClass);
				}
			}
			scWidgetUtils.removeClass(screen,repPanelUId,"errorRepeatingPanel");			
			screen[lastScannedPanelValueVar] = repPanelUId;
		
		};
		
		util.handleProductScan = function(screen,inputModel,lastScannedProductPanelUId,lastScannedNamespace,repPanelUId,repScreenNamespace,message){
			var shipmentLine = {};			
			shipmentLine.ShipmentLine = inputModel.BarCode.Shipment.ShipmentLine;
			var lastScannedProductPanelScreen = scScreenUtils.getChildScreen(screen,lastScannedProductPanelUId);
			if(scBaseUtils.isVoid(lastScannedProductPanelScreen)){
				scScreenUtils.showChildScreen(screen,lastScannedProductPanelUId,"","",{},{});
				lastScannedProductPanelScreen = scScreenUtils.getChildScreen(screen, lastScannedProductPanelUId);			
			}
			if(!scWidgetUtils.isWidgetVisible(screen,lastScannedProductPanelUId)){
				scWidgetUtils.showWidget(screen,lastScannedProductPanelUId,false,"");
			}
			lastScannedProductPanelScreen.setModel(lastScannedNamespace,shipmentLine,"");
			scScreenUtils.clearScreen(lastScannedProductPanelScreen);
			scEventUtils.fireEventInsideScreen(lastScannedProductPanelScreen, "updateLastProductScanned", null, {});
			
			var repScreenUId = scRepeatingPanelUtils.returnUIdOfIndividualRepeatingPanel(screen,repPanelUId,shipmentLine.ShipmentLine.ShipmentLineKey);
			var repScreen = iasScreenUtils.getRepeatingPanelScreenWidget(screen,repScreenUId);
			if(!scBaseUtils.isVoid(repScreen)){
				repScreen.setModel(repScreenNamespace,shipmentLine);
				scScreenUtils.clearScreen(repScreen);
				scEventUtils.fireEventInsideScreen(repScreen, "handleUpdateShipmentLine", null, {});
				iasScreenUtils.toggleHighlight(screen,repScreen,"lastScannedShipmentLineScreenUId","errorMsgPnl","success",message);
			}
			
			//util.highlightLastScannedRepeatingPanel(screen,"lastScannedShipmentLineScreenUId",repScreenUId,"highlightRepeatingPanel");
			
		};
		
		util.handleUpdateShipmentLine = function(screen,inputModel,lastScannedProductPanelUId,lastScannedNamespace,repPanelUId,repScreenNamespace,message){
			var shipmentLine = {};			
			shipmentLine.ShipmentLine = inputModel.Shipment.ShipmentLines.ShipmentLine[0];
			var lastScannedProductPanelScreen = scScreenUtils.getChildScreen(screen,lastScannedProductPanelUId);
			if(!scBaseUtils.isVoid(lastScannedProductPanelScreen)){
				var lastScannedProductModel = scScreenUtils.getModel(lastScannedProductPanelScreen,lastScannedNamespace);
				if(lastScannedProductModel.ShipmentLine.ShipmentLineKey === shipmentLine.ShipmentLine.ShipmentLineKey){
					lastScannedProductPanelScreen.setModel(lastScannedNamespace,shipmentLine,"");
					scScreenUtils.clearScreen(lastScannedProductPanelScreen);
					scEventUtils.fireEventInsideScreen(lastScannedProductPanelScreen, "updateLastProductScanned", null, {});
				}
			}
			var repScreenUId = scRepeatingPanelUtils.returnUIdOfIndividualRepeatingPanel(screen,repPanelUId,shipmentLine.ShipmentLine.ShipmentLineKey);
			var repScreen = iasScreenUtils.getRepeatingPanelScreenWidget(screen,repScreenUId);
			if(!scBaseUtils.isVoid(repScreen)){
				repScreen.setModel(repScreenNamespace,shipmentLine);
				scScreenUtils.clearScreen(repScreen);
				scEventUtils.fireEventInsideScreen(repScreen, "handleUpdateShipmentLine", null, {});
				iasScreenUtils.toggleHighlight(screen, repScreen,"lastScannedShipmentLineScreenUId","errorMsgPnl","success",message);
			}
			else if(!scBaseUtils.isVoid(lastScannedProductPanelScreen)){
				iasBaseTemplateUtils.displaySingleMessage(lastScannedProductPanelScreen, message, "success", null, "errorMsgPnl");
			}
			//util.highlightLastScannedRepeatingPanel(screen,"lastScannedShipmentLineScreenUId",repScreenUId,"highlightRepeatingPanel");
			
		};
				
		util.importShipmentLineToShipment = function(shipmentModel,shipmentLineModel){
			var shipment = scBaseUtils.cloneModel(shipmentModel);
			var shipmentLine =  scBaseUtils.cloneModel(shipmentLineModel);
			if(!scBaseUtils.isVoid(shipment.Shipment.ShipmentLines) && !scBaseUtils.isVoid(shipment.Shipment.ShipmentLines.ShipmentLine)){
				shipment.Shipment.ShipmentLines.ShipmentLine[shipment.Shipment.ShipmentLines.ShipmentLine.length]=shipmentLine.ShipmentLine;			
			}
			else{
				shipment.Shipment.ShipmentLines = {};
				shipment.Shipment.ShipmentLines.ShipmentLine = [];
				shipment.Shipment.ShipmentLines.ShipmentLine[0] = shipmentLine.ShipmentLine;			
			}
			return shipment;
		};
		
		util.getNameDisplay = function(screen, model){
			var country = scUserprefs.getCountry();
			var key = "CustomerNameFormat";
			if(country){
				key = country + "_" + "CustomerNameFormat";
				if (!scScreenUtils.hasBundleKey || !scScreenUtils.hasBundleKey(screen, key)){
					key = "CustomerNameFormat";
				}
			}
			if (scBaseUtils.isVoid(model.FirstName) || scBaseUtils.isVoid(model.LastName)){
				if (!scBaseUtils.isVoid(model.FirstName)){
					return model.FirstName;
				} else if (!scBaseUtils.isVoid(model.LastName)){
					return model.LastName;
				}	
				return " ";		
			} 
			var response = scScreenUtils.getFormattedString(screen, key, model);
			if(response == key){
				response = scScreenUtils.getFormattedString(screen, "CustomerNameFormat", model);
			}
			if(response == "   "){
				return " ";
			}
			return response;
		};
		
		util.handleStartPack_OnClick = function(screen, shipmentInputModel, getShipmentDetailsMashup) {
            var mashupContext = scControllerUtils.getMashupContext(screen);
            var apiInput = scBaseUtils.getNewModelInstance();
            var shipmentKey = scBaseUtils.getAttributeValue("Shipment.ShipmentKey", false, shipmentInputModel);
            scBaseUtils.setAttributeValue("Shipment.ShipmentKey", shipmentKey, apiInput);
            scBaseUtils.setAttributeValue("Shipment.Action", "Pack", apiInput);
            scBaseUtils.setAttributeValue("Action", "Pack", mashupContext);
            iasUIUtils.callApi(screen, apiInput, getShipmentDetailsMashup, mashupContext);
    };
    
    util.handleBackroomPick_OnClick = function(screen, shipmentInputModel, getShipmentDetailsMashup) {
        var mashupContext = scControllerUtils.getMashupContext(screen);
        var apiInput = scBaseUtils.getNewModelInstance();
        var shipmentKey = scBaseUtils.getAttributeValue("Shipment.ShipmentKey", false, shipmentInputModel);
        scBaseUtils.setAttributeValue("Shipment.ShipmentKey", shipmentKey, apiInput);
        scModelUtils.setStringValueAtModelPath("Shipment.Action", "BackroomPick", apiInput);
        scBaseUtils.addStringValueToBean("Action", "BackroomPick", mashupContext);
        iasUIUtils.callApi(screen, apiInput, getShipmentDetailsMashup, mashupContext);
    };
    
    util.handleCustomerPick_OnClick = function(screen, shipmentInputModel, getShipmentDetailsMashup) {
        var apiInput = scBaseUtils.getNewModelInstance();
        var apiInput = scModelUtils.createModelObjectFromKey("Shipment", apiInput);
        var mashupContext = scControllerUtils.getMashupContext(screen);
        scModelUtils.setStringValueAtModelPath("Shipment.ShipmentKey", scModelUtils.getStringValueFromPath("Shipment.ShipmentKey", shipmentInputModel), apiInput);
        scModelUtils.setStringValueAtModelPath("Shipment.Action", "CustomerPick", apiInput);
        scBaseUtils.addStringValueToBean("Action", "CustomerPick", mashupContext);
        iasUIUtils.callApi(screen, apiInput, getShipmentDetailsMashup, mashupContext);
    };
    
    util.handleValidationOutput = function(screen,mashupRefId,modelOutput, mashupInput, mashupContext, applySetModel){
    	var errorElement = null;
        var errorDescription = null;
        var action = scBaseUtils.getAttributeValue("Action", false, mashupContext);
        var errorElement = scModelUtils.getModelObjectFromPath("Shipment.Error", modelOutput);
        if (!(scBaseUtils.isVoid(errorElement))) {
            errorDescription = scModelUtils.getStringValueFromPath("ErrorDescription", errorElement);
            if (scBaseUtils.equals(errorDescription, "AssignedToUserIsDifferent")) {
                if (scBaseUtils.equals(action, "Pack") || scBaseUtils.equals(action, "BackroomPick")) {
                    var popupParams = scBaseUtils.getNewBeanInstance();
                    scBaseUtils.addStringValueToBean("outputNamespace", "popupData", popupParams);
                    var dialogParams = scBaseUtils.getNewBeanInstance();
                    var bindings = scBaseUtils.getNewBeanInstance();
                    scBaseUtils.addModelValueToBean("Shipment", modelOutput, bindings);
                    scBaseUtils.addStringValueToBean("Action", action, bindings);
                    scBaseUtils.addBeanValueToBean("binding", bindings, popupParams);
                    scBaseUtils.setAttributeValue("closeCallBackHandler", "abandonedCallbackHandler", dialogParams);
                    scBaseUtils.setAttributeValue("class", "idxModalDialog", dialogParams);
                    iasUIUtils.openSimplePopup("wsc.components.shipment.common.screens.PickorPackWarningDialog", "Label_warning_message", screen, popupParams, dialogParams);
                }
            } else {
                iasBaseTemplateUtils.showMessage(screen, errorDescription, "error", null);
            }
        } else {
            if (scBaseUtils.equals(action, "Unpack")) {
               iasUIUtils.openWizardInEditor("wsc.components.shipment.container.unpack.UnpackShipmentWizard", modelOutput, "wsc.desktop.editors.ShipmentEditor", screen, false);
            } else if (scBaseUtils.equals(action, "Pack")) {
               iasUIUtils.openWizardInEditor("wsc.components.shipment.container.pack.ContainerPackWizard", modelOutput, "wsc.desktop.editors.ShipmentEditor", screen);
            } else if (scBaseUtils.equals(action, "BackroomPick")) {
               iasUIUtils.openWizardInEditor("wsc.components.shipment.backroompick.BackroomPickupWizard", modelOutput, "wsc.desktop.editors.ShipmentEditor", screen, false);
            } else if (scBaseUtils.equals(action, "CustomerPick")) {
               iasUIUtils.openWizardInEditor("wsc.components.shipment.customerpickup.CustomerPickUpWizard", modelOutput, "wsc.desktop.editors.ShipmentEditor", screen);
            }
        }
    };
    
    util.handleShipDetailsOutput = function(screen,modelOutput,errorLabelUId){
    	var errorElement = null;
        var errorDescription = null;
        var errorElement = scModelUtils.getModelObjectFromPath("Shipment.Error", modelOutput);
        var action = scModelUtils.getStringValueFromPath("Action", errorElement);
        if (!(scBaseUtils.isVoid(errorElement))) {
            errorDescription = scModelUtils.getStringValueFromPath("ErrorDescription", errorElement);
            if (scBaseUtils.equals(errorDescription, "AssignedToUserIsDifferent")) {
                if (scBaseUtils.equals(action, "Pack") || scBaseUtils.equals(action, "BackroomPick")) {
                    var popupParams = scBaseUtils.getNewBeanInstance();
                    scBaseUtils.addStringValueToBean("outputNamespace", "popupData", popupParams);
                    var dialogParams = scBaseUtils.getNewBeanInstance();
                    var bindings = scBaseUtils.getNewBeanInstance();
                    scBaseUtils.addModelValueToBean("Shipment", modelOutput, bindings);
                    scBaseUtils.addStringValueToBean("Action", action, bindings);
                    scBaseUtils.addBeanValueToBean("binding", bindings, popupParams);
                    scBaseUtils.setAttributeValue("closeCallBackHandler", "abandonedCallbackHandler", dialogParams);
                    scBaseUtils.setAttributeValue("class", "idxModalDialog", dialogParams);
                    
                    iasUIUtils.openSimplePopup("wsc.components.shipment.common.screens.PickorPackWarningDialog", "Label_warning_message", screen, popupParams, dialogParams);
                }
            } 
            else if(scBaseUtils.equals(errorDescription, "ShipmentIncludedInBatch")){
            	var popupParams = scBaseUtils.getNewBeanInstance();
                scBaseUtils.addStringValueToBean("outputNamespace", "popupData", popupParams);
                var dialogParams = scBaseUtils.getNewBeanInstance();
                var bindings = scBaseUtils.getNewBeanInstance();
                scBaseUtils.addModelValueToBean("Shipment", modelOutput, bindings);
                scBaseUtils.addStringValueToBean("Action", action, bindings);
                scBaseUtils.addBeanValueToBean("binding", bindings, popupParams);
                scBaseUtils.setAttributeValue("closeCallBackHandler", "abandonedCallbackHandler", dialogParams);
                scBaseUtils.setAttributeValue("class", "idxConfirmDialog fixedActionBarDialog", dialogParams);
                iasUIUtils.openSimplePopup("wsc.components.shipment.common.screens.BatchPickStartOverDialog", "Label_warning_message", screen, popupParams, dialogParams);
            }
            else {
                scWidgetUtils.showWidget(screen, errorLabelUId, true, null);
                scWidgetUtils.setLabelText(screen, errorLabelUId, errorDescription, true);
            }
        } 
    };
    	
    util.showNextTask = function(screen, shipmentModel, shipNodePath, startBRPLink,contBRPLink,pickTicketLink,startPackLink,contPackLink,custPickLink) {
    	//var status = scModelUtils.getStringValueFromPath("Shipment.Status.Status", shipmentModel);
        var deliveryMethod = scModelUtils.getStringValueFromPath("Shipment.DeliveryMethod", shipmentModel);
        var shipNode = scModelUtils.getStringValueFromPath(shipNodePath, shipmentModel);
       /* if(scBaseUtils.isVoid(shipNode))
        	shipNode = scModelUtils.getStringValueFromPath("Shipment.ShipNode.ShipNode", shipmentModel);*/
        var currentStore = iasContextUtils.getFromContext("CurrentStore");
        var allowedTransactions = scModelUtils.getModelListFromPath("Shipment.AllowedTransactions.Transaction",shipmentModel)
        var allowedTransactionList = [];
        for(i=0;i<allowedTransactions.length;i++){
        	allowedTransactionList.push(scModelUtils.getStringValueFromPath("Tranid",allowedTransactions[i]));
        }
        var brpPermission = null;
        var printPermission = null;
        if (scBaseUtils.equals(deliveryMethod, "SHP")) {
            brpPermission = "WSC000017";
            printPermission = "WSC000021";
        } else {
            brpPermission = "WSC000006";
            printPermission = "WSC000009";
        }
        
        if(scBaseUtils.equals(currentStore, shipNode)){
        	if(dArray.indexOf(allowedTransactionList,"YCD_BACKROOM_PICK_IN_PROGRESS") > -1){
             	if(scResourcePermissionUtils.hasPermission(brpPermission))
             		scWidgetUtils.showWidget(screen, startBRPLink, true, null);
             	if(scResourcePermissionUtils.hasPermission(printPermission))
             		scWidgetUtils.showWidget(screen, pickTicketLink, true, null);
             } else if(dArray.indexOf(allowedTransactionList,"YCD_BACKROOM_PICK") > -1 && scResourcePermissionUtils.hasPermission(brpPermission)){
             	scWidgetUtils.showWidget(screen, contBRPLink, true, null);
             }else if(dArray.indexOf(allowedTransactionList,"YCD_UNDO_BACKROOM_PICK") > -1 && dArray.indexOf(allowedTransactionList,"PACK_SHIPMENT_COMPLETE") > -1){
             	scWidgetUtils.showWidget(screen, startPackLink, true, null);
             }else if(dArray.indexOf(allowedTransactionList,"PACK_SHIPMENT_COMPLETE") > -1 && dArray.indexOf(allowedTransactionList,"UNDO_PACK_SHMT_COMPLETE") > -1){
             	scWidgetUtils.showWidget(screen, contPackLink, true, null);
             //}else if(dArray.indexOf(allowedTransactionList,"CONFIRM_SHIPMENT") > -1 && scBaseUtils.equals(deliveryMethod, "PICK") && !iasContextUtils.isMobileContainer()){
			 }else if(dArray.indexOf(allowedTransactionList,"CONFIRM_SHIPMENT") > -1 && scBaseUtils.equals(deliveryMethod, "PICK")){
             	scWidgetUtils.showWidget(screen, custPickLink, true, null);
             }
        }
       
        	/*if (scBaseUtils.equals(status, "1100.70.06.10")) {
            if (scResourcePermissionUtils.hasPermission(brpPermission) && scBaseUtils.equals(currentStore, shipNode)) {
                scWidgetUtils.showWidget(screen, "lnkStartBRP", true, null);
            }
			if (scResourcePermissionUtils.hasPermission(printPermission) && scBaseUtils.equals(currentStore, shipNode)) {
			scWidgetUtils.showWidget(screen, "lnkPrint", true, null);
			}
        } else if (scBaseUtils.equals(status, "1100.70.06.20")) {
            if (scResourcePermissionUtils.hasPermission(brpPermission) && scBaseUtils.equals(currentStore, shipNode)) {
                scWidgetUtils.showWidget(screen, "lnkContinueBRP", true, null);
            }
        } else if (scBaseUtils.equals(status, "1100.70.06.30")) {
            if (scResourcePermissionUtils.hasPermission("WSC000001") && scBaseUtils.equals(currentStore, shipNode)) {
                scWidgetUtils.showWidget(screen, "lnkStartCustomerPickup", true, null);
            }
        } else if (scBaseUtils.equals(status, "1100.70.06.50")) {
            if (scBaseUtils.equals(currentStore, shipNode)) {
                scWidgetUtils.showWidget(screen, "lnkPack", true, null);
            }
        } else if (scBaseUtils.equals(status, "1100.70.06.70")) {
            if (scBaseUtils.equals(currentStore, shipNode)) {
                scWidgetUtils.showWidget(screen, "lnkContinuePack", true, null);
            }
        }*/
    };
    
    /**
     * unpackMashup=containerPack_unpackShipment
     * packChangeShpMashup=containerPack_changeShipment
     * packChangeShpStatusMashup=containerPack_changeShipmentStatus
     * bpChangeShpMashup=changeShipmentToUpdateQty   
     */
    util.handleAssignedUserPopupResponse = function(screen, actionPerformed, outputModel, popupParams, unpackMashup, 
    		packChangeShpMashup, packChangeShpStatusMashup, bpChangeShpMashup){
    	if (!(scBaseUtils.equals(actionPerformed, "CLOSEPOPUP"))) {
                    var modelList = null;
                    var mashupRefList = null;
                    var mashupContext = scControllerUtils.getMashupContext(screen);
                    var shipmentModel = scBaseUtils.getAttributeValue("binding.Shipment", false, popupParams);
                    var action = scBaseUtils.getAttributeValue("binding.Action", false, popupParams);
                    scBaseUtils.addStringValueToBean("Action", action, mashupContext);
                    var apiInput = scBaseUtils.getNewModelInstance();
                    scModelUtils.setStringValueAtModelPath("Shipment.ShipmentKey", scBaseUtils.getAttributeValue("Shipment.ShipmentKey", false, shipmentModel), apiInput);
                    if (scBaseUtils.equals(actionPerformed, "STARTOVER")) {
                        modelList = scBaseUtils.getNewArrayInstance();
                        mashupRefList = scBaseUtils.getNewArrayInstance();
                        if (scBaseUtils.equals(action, "Pack")) {
                            if (!(scBaseUtils.equals(scModelUtils.getNumberValueFromPath("Shipment.ShipmentContainerizedFlag", shipmentModel), 1))) {
                                var containerPack_unpackShipment_Input = null;
                                containerPack_unpackShipment_Input = scBaseUtils.getNewModelInstance();
                                scModelUtils.setStringValueAtModelPath("Container.ShipmentKey", scModelUtils.getStringValueFromPath("Shipment.ShipmentKey", apiInput), containerPack_unpackShipment_Input);
                                scBaseUtils.appendToArray(modelList, containerPack_unpackShipment_Input);
                                scBaseUtils.appendToArray(mashupRefList, unpackMashup);
                            }
                            scBaseUtils.appendToArray(modelList, apiInput);
                            scBaseUtils.appendToArray(mashupRefList, packChangeShpMashup);
                            if (!(util.startsWith(scBaseUtils.getAttributeValue("Shipment.Status", false, shipmentModel), "1100.70.06.70"))) {
                                scBaseUtils.appendToArray(modelList, apiInput);
                                scBaseUtils.appendToArray(mashupRefList, packChangeShpStatusMashup);
                            }
                            iasUIUtils.callApis(screen, modelList, mashupRefList, mashupContext, null);
                        } else if (scBaseUtils.equals(action, "BackroomPick")) {
                            scBaseUtils.addStringValueToBean("Action", action, mashupContext);
                            scModelUtils.setStringValueAtModelPath("Shipment.Action", "StartOver", apiInput);
                            iasUIUtils.callApi(screen, apiInput, bpChangeShpMashup, mashupContext);
                        }
                    } else if (scBaseUtils.equals(actionPerformed, "CONTINUE")) {
                        if (scBaseUtils.equals(action, "Pack")) {
                            if (!(util.startsWith(scBaseUtils.getAttributeValue("Shipment.Status", false, shipmentModel), "1100.70.06.70"))) {
                                scBaseUtils.appendToArray(modelList, apiInput);
                                scBaseUtils.appendToArray(mashupRefList, packChangeShpStatusMashup);
                                scBaseUtils.appendToArray(modelList, apiInput);
                                scBaseUtils.appendToArray(mashupRefList, packChangeShpMashup);
                                iasUIUtils.callApis(screen, modelList, mashupRefList, mashupContext, null);
                            } else {
                                iasUIUtils.callApi(screen, apiInput, packChangeShpMashup, mashupContext);
                            }
                        } else if (scBaseUtils.equals(action, "BackroomPick")) {
                            scBaseUtils.addStringValueToBean("Action", action, mashupContext);
                            scModelUtils.setStringValueAtModelPath("Shipment.Action", "Continue", apiInput);
                            iasUIUtils.callApi(screen, apiInput, bpChangeShpMashup, mashupContext);
                        }
                    }
                }
    };
    
    util.handleAssignedUserPopupAndNext = function(screen, actionPerformed, outputModel, popupParams, unpackMashup, 
    		packChangeShpMashup, packChangeShpStatusMashup, bpChangeShpMashup){
    	if (!(scBaseUtils.equals(actionPerformed, "CLOSEPOPUP"))) {
                    var modelList = null;
                    var mashupRefList = null;
                    mashupRefList = scBaseUtils.getNewArrayInstance();
                    var mashupContext = scControllerUtils.getMashupContext(screen);
                    var args = null;
                    var screenId = scBaseUtils.getValueFromPath("declaredClass",screen);
                    var shipmentModel = scBaseUtils.getAttributeValue("binding.Shipment", false, popupParams);
                    var action = scBaseUtils.getAttributeValue("binding.Action", false, popupParams);
                    scBaseUtils.addStringValueToBean("Action", action, mashupContext);
                    var apiInput = scBaseUtils.getNewModelInstance();
                    scModelUtils.setStringValueAtModelPath("Shipment.ShipmentKey", scBaseUtils.getAttributeValue("Shipment.ShipmentKey", false, shipmentModel), apiInput);
                    if (scBaseUtils.equals(actionPerformed, "STARTOVER")||scBaseUtils.equals(actionPerformed, "STARTOVERFROMBATCH")) {
                        modelList = scBaseUtils.getNewArrayInstance();
                        
                        if (scBaseUtils.equals(action, "Pack")) {
                            if (!(scBaseUtils.equals(scModelUtils.getNumberValueFromPath("Shipment.ShipmentContainerizedFlag", shipmentModel), 1))) {
                                var containerPack_unpackShipment_Input = null;
                                containerPack_unpackShipment_Input = scBaseUtils.getNewModelInstance();
                                scModelUtils.setStringValueAtModelPath("Container.ShipmentKey", scModelUtils.getStringValueFromPath("Shipment.ShipmentKey", apiInput), containerPack_unpackShipment_Input);
                              //  scBaseUtils.appendToArray(modelList, containerPack_unpackShipment_Input);
                                //scBaseUtils.appendToArray(mashupRefList, unpackMashup);
                                var newModelData = scBaseUtils.getNewModelInstance();
                            	scModelUtils.addStringValueToModelObject("mashupRefId",unpackMashup,newModelData);
                            	scModelUtils.addStringValueToModelObject("mashupInput",containerPack_unpackShipment_Input,newModelData);
                            	scBaseUtils.appendToArray(mashupRefList,newModelData);
                            }
                           // scBaseUtils.appendToArray(modelList, apiInput);
                            //scBaseUtils.appendToArray(mashupRefList, packChangeShpMashup);
                                var newModelData1 = scBaseUtils.getNewModelInstance();
                                scModelUtils.addStringValueToModelObject("mashupRefId",packChangeShpMashup,newModelData1);
                            	scModelUtils.addStringValueToModelObject("mashupInput",apiInput,newModelData1);
                            	scBaseUtils.appendToArray(mashupRefList,newModelData1);
                            if (!(util.startsWith(scBaseUtils.getAttributeValue("Shipment.Status", false, shipmentModel), "1100.70.06.70"))) {
                               // scBaseUtils.appendToArray(modelList, apiInput);
                                //scBaseUtils.appendToArray(mashupRefList, packChangeShpStatusMashup);
                                var newModelData2 = scBaseUtils.getNewModelInstance();
                                scModelUtils.addStringValueToModelObject("mashupRefId",packChangeShpStatusMashup,newModelData2);
                            	scModelUtils.addStringValueToModelObject("mashupInput",apiInput,newModelData2);
                            	scBaseUtils.appendToArray(mashupRefList,newModelData2);
                            }
                            //iasUIUtils.callApis(screen, modelList, mashupRefList, mashupContext, null);
                             args = iasUIUtils.formatArgForUpdateAndNextCall(screenId,mashupRefList);
                             scEventUtils.fireEventToParent(screen,"combinedAPICallOnNext",args);
                        } else if (scBaseUtils.equals(action, "BackroomPick")|| scBaseUtils.equals(action, "BackroomPickIncludedInBatch") ) {
                            //scBaseUtils.addStringValueToBean("Action", action, mashupContext);
                            if(scBaseUtils.equals(action, "BackroomPick"))
                            	scModelUtils.setStringValueAtModelPath("Shipment.Action", "StartOver", apiInput);
                            else if(scBaseUtils.equals(action, "BackroomPickIncludedInBatch")){
                            	scModelUtils.setStringValueAtModelPath("Shipment.Action", "StartOverFromBatchPick", apiInput);
                            	var storeBatchList = shipmentModel.Shipment.StoreBatchList.StoreBatch;
                            	apiInput.Shipment.StoreBatchList={};
                            	apiInput.Shipment.StoreBatchList.StoreBatch=[];
                            	for(i=0;i<storeBatchList.length;i++){
                            		var sBatchKey = storeBatchList[i].StoreBatchKey;
                            		apiInput.Shipment.StoreBatchList.StoreBatch[i]={};
                            		apiInput.Shipment.StoreBatchList.StoreBatch[i].StoreBatchKey=sBatchKey;
                            	}
                            	
                            }	
                           // iasUIUtils.callApi(screen, apiInput, bpChangeShpMashup, mashupContext);
                            var newModelData = scBaseUtils.getNewModelInstance();
                            scModelUtils.addStringValueToModelObject("mashupRefId",bpChangeShpMashup,newModelData);
                            scModelUtils.addStringValueToModelObject("mashupInput",apiInput,newModelData);
                            scBaseUtils.appendToArray(mashupRefList,newModelData);
                            args = iasUIUtils.formatArgForUpdateAndNextCall(screenId,mashupRefList);
                            scEventUtils.fireEventToParent(screen,"combinedAPICallOnNext",args);
                            
                        }
                    } else if (scBaseUtils.equals(actionPerformed, "CONTINUE")) {
                        if (scBaseUtils.equals(action, "Pack")) {
                        	var newModelData = scBaseUtils.getNewModelInstance();
                        	scModelUtils.addStringValueToModelObject("mashupRefId",packChangeShpMashup,newModelData);
                        	scModelUtils.addStringValueToModelObject("mashupInput",apiInput,newModelData);
                        	scBaseUtils.appendToArray(mashupRefList,newModelData);
                            if (!(util.startsWith(scBaseUtils.getAttributeValue("Shipment.Status", false, shipmentModel), "1100.70.06.70"))) {
                               // scBaseUtils.appendToArray(modelList, apiInput);
                               // scBaseUtils.appendToArray(mashupRefList, packChangeShpStatusMashup);
                               // scBaseUtils.appendToArray(modelList, apiInput);
                               // scBaseUtils.appendToArray(mashupRefList, packChangeShpMashup);
                               // iasUIUtils.callApis(screen, modelList, mashupRefList, mashupContext, null);
                               var newModelData1 = scBaseUtils.getNewModelInstance();
                            	scModelUtils.addStringValueToModelObject("mashupRefId",packChangeShpStatusMashup,newModelData1);
                            	scModelUtils.addStringValueToModelObject("mashupInput",apiInput,newModelData1);
                            	scBaseUtils.appendToArray(mashupRefList,newModelData1);
                            	args = iasUIUtils.formatArgForUpdateAndNextCall(screenId,mashupRefList);
                            scEventUtils.fireEventToParent(screen,"combinedAPICallOnNext",args);
                               
                            } else {
                                //iasUIUtils.callApi(screen, apiInput, packChangeShpMashup, mashupContext);
                                args = iasUIUtils.formatArgForUpdateAndNextCall(screenId,mashupRefList);
                            	scEventUtils.fireEventToParent(screen,"combinedAPICallOnNext",args);
                            }
                        } else if (scBaseUtils.equals(action, "BackroomPick")) {
                            //scBaseUtils.addStringValueToBean("Action", action, mashupContext);
                            scModelUtils.setStringValueAtModelPath("Shipment.Action", "Continue", apiInput);
                            //iasUIUtils.callApi(screen, apiInput, bpChangeShpMashup, mashupContext);
                            var newModelData = scBaseUtils.getNewModelInstance();
                            scModelUtils.addStringValueToModelObject("mashupRefId",bpChangeShpMashup,newModelData);
                            scModelUtils.addStringValueToModelObject("mashupInput",apiInput,newModelData);
                            scBaseUtils.appendToArray(mashupRefList,newModelData);
                            args = iasUIUtils.formatArgForUpdateAndNextCall(screenId,mashupRefList);
                            scEventUtils.fireEventToParent(screen,"combinedAPICallOnNext",args);
                        }
                    }
                } else if(scBaseUtils.equals(actionPerformed, "STARTOVERFROMBATCH")){
                	scModelUtils.setStringValueAtModelPath("Shipment.Action", "StartOverBatchPick", apiInput);
                	var newModelData = scBaseUtils.getNewModelInstance();
                	
                }
                else{
                	var currentEditor = scEditorUtils.getCurrentEditor();
			    	 var scr = scEditorUtils.getScreenInstance(currentEditor);
			 		var wizInstance =scr.getWizardElemForId(scr.wizardId).wizardInstance;
			 		//scWizardUtils.closeWizard(wizInstance);
					iasWizardUtils.handleWizardCloseConfirmation(wizInstance,"Ok",{});
			 		
                }
    };
    
    util.openBackroomPickWizard = function(screen,shipmentModel){
    	iasUIUtils.openWizardInEditor("wsc.components.shipment.backroompick.BackroomPickupWizard", shipmentModel, "wsc.desktop.editors.ShipmentEditor", screen, false);
    };
    
    util.openPackingWizard = function(screen,shipmentModel){
    	iasUIUtils.openWizardInEditor("wsc.components.shipment.container.pack.ContainerPackWizard", shipmentModel, "wsc.desktop.editors.ShipmentEditor", screen);
    };
    
    util.showSLA = function(status){
    	if(scBaseUtils.contains(status,"1100.70.06.10") || scBaseUtils.contains(status,"1100.70.06.20") 
    			|| scBaseUtils.contains(status,"1100.70.06.30") || scBaseUtils.contains(status,"1100.70.06.50") 
    			|| scBaseUtils.contains(status,"1100.70.06.70") || scBaseUtils.contains(status,"1300"))
    		return true;
    	else
    		return false;
    };
	
    util.DisplayOrderNoForShipSummary = function(orderNo){
    	
    	var orderNoArray = orderNo.split("|");
    	if (( orderNoArray.length == 1 ) && iasContextUtils.isMobileContainer()) {
			return "";
		}else{
			var formattedOrderNo = "";
			formattedOrderNo =wscCommonUtils.getDisplayOrderNumber(orderNo,"COMMAS");
			return formattedOrderNo;
		}
    };
    
	util.showHideHoldLocation = function(screen,shipmentModel,widgetUId){
		var status = shipmentModel.Shipment.Status.Status?shipmentModel.Shipment.Status.Status:shipmentModel.Shipment.Status;
		var holdLocation = shipmentModel.Shipment.HoldLocation;
		if(!scBaseUtils.isVoid(holdLocation) && (scBaseUtils.contains(status,"1100.70.06.10") || scBaseUtils.contains(status,"1100.70.06.20") 
    			|| scBaseUtils.contains(status,"1100.70.06.30") || scBaseUtils.contains(status,"1100.70.06.50") 
    			|| scBaseUtils.contains(status,"1100.70.06.70")))
    		scWidgetUtils.showWidget(screen, widgetUId, true, null);
    	else
    		scWidgetUtils.hideWidget(screen, widgetUId, true);
	};
	
	util.showHideIncludedInBatch = function(dataValue, screen, widget, nameSpace, shipmentModel, options) {
		var literal = dataValue;
    	var status = shipmentModel.Shipment.Status.Status?shipmentModel.Shipment.Status.Status:shipmentModel.Shipment.Status;
		var inclInBatch = shipmentModel.Shipment.IncludedInBatch;
		if(!scBaseUtils.isVoid(inclInBatch) && (scBaseUtils.contains(status,"1100.70.06.10") || scBaseUtils.contains(status,"1100.70.06.20") 
    			|| scBaseUtils.contains(status,"1100.70.06.30") || scBaseUtils.contains(status,"1100.70.06.50") )){
			scWidgetUtils.showWidget(screen, widget, true, null);
			literal = scScreenUtils.getString( screen, "Label_Included_In_Batch");
		}else{
			scWidgetUtils.hideWidget(screen, widget, true);
		}
    		
        return literal;
    };
	
	util.getShipmentStatusDescription = function(unformattedValue,screen,widget,namespace,shipmentModel,options){
		var status = shipmentModel.Shipment.Status.Status?shipmentModel.Shipment.Status.Status:shipmentModel.Shipment.Status;
		var deliveryMethod = shipmentModel.Shipment.DeliveryMethod;
		if(status && deliveryMethod && scBundleUtils.hasBundleKey("Label_"+status+"_"+deliveryMethod)){
			return scBundleUtils.getString("Label_"+status+"_"+deliveryMethod);
		}
		else if(status && scBundleUtils.hasBundleKey("Label_"+status)){
			return scBundleUtils.getString("Label_"+status);
		}
		else {
			return unformattedValue;
		}	
	};
    
		return util;
	});