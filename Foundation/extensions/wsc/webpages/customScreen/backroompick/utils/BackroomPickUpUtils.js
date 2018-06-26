/*
 * Licensed Materials - Property of IBM
 * IBM Sterling Order Management Store (5725-D10)
 *(C) Copyright IBM Corp. 2014 , 2015 All Rights Reserved. , 2015 All Rights Reserved.
 * US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 */
scDefine([
	"scbase/loader!dojo/_base/lang",	
	"scbase/loader!wsc",	
	"scbase/loader!sc/plat/dojo/Userprefs",
	"scbase/loader!sc/plat/dojo/utils/BaseUtils",
	"scbase/loader!sc/plat/dojo/utils/BundleUtils",
	"scbase/loader!sc/plat/dojo/utils/ScreenUtils",
	"scbase/loader!sc/plat/dojo/utils/ModelUtils",	
	"scbase/loader!ias/utils/UIUtils",
	"scbase/loader!ias/utils/ContextUtils",
	"scbase/loader!ias/utils/UOMUtils",
	"scbase/loader!sc/plat/dojo/utils/WidgetUtils",
	"scbase/loader!sc/plat/dojo/utils/EventUtils",
	"scbase/loader!sc/plat/dojo/widgets/Screen",
	"scbase/loader!sc/plat/dojo/utils/ControllerUtils",
	"scbase/loader!ias/utils/BaseTemplateUtils",
	"scbase/loader!dojo/NodeList-manipulate",
	"scbase/loader!dojox/html/entities"
	],
	function(dLang,wsc,scUserprefs,scBaseUtils,scBundleUtils,scScreenUtils,scModelUtils,iasUIUtils,iasContextUtils, iasUOMUtils,scWidgetUtils,scEventUtils,scScreen,scControllerUtils,iasBaseTemplateUtils,dHtmlEntities){
		var backroomPickUtils = dLang.getObject("components.shipment.backroompick.utils.BackroomPickUpUtils", true,wsc);
		
		backroomPickUtils.getFormattedRemainingQuantity = function(screen,shipmentLineModel) {
			
			var remainingQtyWithUOM = "";
			var uomDisplayFormat="";
			var backroomPickedQty = 0;

			if(!scBaseUtils.isVoid(scModelUtils.getStringValueFromPath("ShipmentLine.BackroomPickedQuantity", shipmentLineModel))) {
				backroomPickedQty = scModelUtils.getNumberValueFromPath("ShipmentLine.BackroomPickedQuantity", shipmentLineModel)
			}
			var uom = scModelUtils.getStringValueFromPath("ShipmentLine.OrderLine.ItemDetails.DisplayUnitOfMeasure", shipmentLineModel);
			
			uomDisplayFormat = scModelUtils.getStringValueFromPath("ShipmentLine.OrderLine.ItemDetails.UOMDisplayFormat", shipmentLineModel);
			var remainingQty = scModelUtils.getNumberValueFromPath("ShipmentLine.Quantity", shipmentLineModel) - backroomPickedQty ;
			remainingQtyWithUOM =  iasUOMUtils.getFormatedQuantityWithUom(remainingQty, uom, uomDisplayFormat);

			return remainingQtyWithUOM; 
		};	
		
		
		backroomPickUtils.getLastUpdatedRepPanelUId = function(screen) {
            var parentScreen = iasUIUtils.getParentScreen(screen, true);					
			if(parentScreen){
				return parentScreen.LastScannedShipmentLineScreenUId;
			}
			return null;
		 };
		
		 backroomPickUtils.setLastUpdatedRepPanelUId = function(screen,value) {
	        var parentScreen = iasUIUtils.getParentScreen(screen, true);
			if(parentScreen){
				parentScreen.LastScannedShipmentLineScreenUId = value;
			}
		 };
		 
		backroomPickUtils.setPickedQuantityInScreen = function(screen, pickedQuantity){
			
			screen.pickedQuantity = pickedQuantity;
			
		};
		
		
		backroomPickUtils.isShipmentStatusValidForAssignHold = function(shipmentStatus){
			
			var isValid = false;
			
			if(!scBaseUtils.isVoid(shipmentStatus) && ( scBaseUtils.equals("1100.70.06.10", shipmentStatus) ||  scBaseUtils.equals("1100", shipmentStatus) || scBaseUtils.equals("1100.70", shipmentStatus))) {
				return isValid;
			}
			

			if ((scBaseUtils.contains(shipmentStatus, "1100.70.06.10")
				|| scBaseUtils.contains(shipmentStatus, "1100.70.06.20") || scBaseUtils.contains(shipmentStatus, "1100.70.06.30") || scBaseUtils.contains(shipmentStatus, "1100.70.06.50"))) {
									
				isValid = true;	
					
			} 
			
			return isValid;
			
		};
		
		
		backroomPickUtils.addCountToLabel = function ( screen, numRecords, labelWithNoCount ) {
			var inputArray = [ numRecords ];
			var completePortletTitle = scScreenUtils.getFormattedString(screen, labelWithNoCount, inputArray);
			return completePortletTitle;
		};
		
		backroomPickUtils.getInProgressWarningString = function(screen,shipmentModel){
		
			var dataArray = scBaseUtils.getNewArrayInstance();
			var assignToUserId = "";
			
			if(!scBaseUtils.isVoid(shipmentModel) && !scBaseUtils.isVoid(shipmentModel.Shipment.AssignedToUserId)) {					
					assignToUserId = 	dHtmlEntities.encode(shipmentModel.Shipment.AssignedToUserId);	
			} else {
				//console.log("ERROR: Unable to retrive AssignToUserId from Shipment");
			}
			scBaseUtils.appendToArray(dataArray, assignToUserId);
			
			var warningString =  scScreenUtils.getFormattedString(screen,"BackroomPickInProgress",dataArray);
			
			return warningString;
			
		};		

		backroomPickUtils.isBackroomPickInProgress = function(screen,shipmentDetailsModel){
		
			var backroomPickInProgressStatus = "1100.70.06.20";
		
			var backroomPickProgressModel = {};
			backroomPickProgressModel.isInProgress = "N";
			backroomPickProgressModel.assignedToDiffUser = "N";
			
			if(!scBaseUtils.isVoid(shipmentDetailsModel) && !scBaseUtils.isVoid(shipmentDetailsModel.Shipment)) {
			
				var status = shipmentDetailsModel.Shipment.Status;
			
				if(!scBaseUtils.isVoid(status) && (status.indexOf(backroomPickInProgressStatus) != -1)) {
					backroomPickProgressModel.isInProgress = "Y";
				}

				if(!scBaseUtils.equals(shipmentDetailsModel.Shipment.AssignedToUserId,scUserprefs.getUserId())) {
					backroomPickProgressModel.assignedToDiffUser = "Y";
				} 
			}
			
			//console.log("loginId : ",scUserprefs.getUserId());
			//console.log("Shipment Details : ",shipmentDetailsModel);
			//console.log("backroomPickProgressModel : ",backroomPickProgressModel);
			
			return backroomPickProgressModel;
		};
		
		backroomPickUtils.openAbandonedShipmentDialog = function(screen, shipmentDetailsModel) {
		
			//console.log("shipmentDetailsModel : ",shipmentDetailsModel);
			
			var popupParams = scBaseUtils.getNewBeanInstance();
			var bindings = scBaseUtils.getNewBeanInstance();
			popupParams.url = null;
			popupParams.Screen = null;
			popupParams.outputNamespace = "popupData";
			popupParams.Shipment = shipmentDetailsModel;
		
			scBaseUtils.addModelValueToBean("Shipment", shipmentDetailsModel, bindings);
			scBaseUtils.addBeanValueToBean("binding", bindings, popupParams);
			
			var dialogParams = scBaseUtils.getNewBeanInstance();
			dialogParams.closeCallBackHandler = "abandonedBPCallbackHandler";
			dialogParams['class'] = "idxConfirmDialog";

			
			iasUIUtils.openSimplePopup("wsc.shipment.backroomPick.AbandonedBackroomPickDialog", "Confirmation", screen, popupParams, dialogParams);
		
		};
		
		
		backroomPickUtils.getChangeShipmentInputToUpdateBPQty = function(screen,shipmentModel) {
		
			var inputModel = {};
			inputModel.Shipment = {};
			inputModel.Shipment.ShipmentLines = {};
			inputModel.Shipment.ShipmentLines.ShipmentLine = [];
			
			if(!scBaseUtils.isVoid(shipmentModel)) {
				
				inputModel.Shipment.ShipmentKey = shipmentModel.Shipment.ShipmentKey;
				
				if(!scBaseUtils.isVoid(shipmentModel.Shipment.ShipmentLines.ShipmentLine) && !scBaseUtils.isVoid(shipmentModel.Shipment.ShipmentLines.ShipmentLine.length)) {
					
					for(var i=0;i<shipmentModel.Shipment.ShipmentLines.ShipmentLine.length;i++) {
						
						var shipmentLineModel = shipmentModel.Shipment.ShipmentLines.ShipmentLine[i];
						inputModel.Shipment.ShipmentLines.ShipmentLine[i] = {};
						inputModel.Shipment.ShipmentLines.ShipmentLine[i].ShipmentLineKey = shipmentLineModel.ShipmentLineKey;
						//changes for handle line marked as shortage in previous backroom pick
						if(!scBaseUtils.isVoid(shipmentLineModel.ShortageQty) && scBaseUtils.numberGreaterThan(Number(shipmentLineModel.ShortageQty),Number("0"))){
							inputModel.Shipment.ShipmentLines.ShipmentLine[i].BackroomPickedQuantity = shipmentLineModel.BackroomPickedQuantity;
						}
						else{
							inputModel.Shipment.ShipmentLines.ShipmentLine[i].BackroomPickedQuantity = "0";
						}
					}
				}
			
			}
			
			//console.log("changeShipmentToUpdateBPQty : ",inputModel);
			
			return inputModel;
		
		};
		
		
		
				
		
		backroomPickUtils.getPrintModel = function(screen, shipmentModel) {
		
			return shipmentModel;
		
		};
		
		backroomPickUtils.createMultiApiInputForPrintService = function(screen, shipmentListModel) {
		
			//console.log("createMultiApiInputForPrintService");
			
			//console.log("shipmentListModel : ",shipmentListModel);
			
			var inputModel = {};
			inputModel.MultiApi = {};
			inputModel.MultiApi.API = [];
			
			if(!scBaseUtils.isVoid(shipmentListModel) && !scBaseUtils.isVoid(shipmentListModel.Shipments.Shipment) && !scBaseUtils.isVoid(shipmentListModel.Shipments.Shipment.length) ) {
			
				var j = 0;
				
				for(var i=0;i<shipmentListModel.Shipments.Shipment.length;i++) {
				
					var shipmentModel = shipmentListModel.Shipments.Shipment[i];
					
					//console.log();
					
					inputModel.MultiApi.API[j] = {};
					inputModel.MultiApi.API[j].Name = "getSortedShipmentDetails";
					inputModel.MultiApi.API[j].Input = {};
					inputModel.MultiApi.API[j].Input = backroomPickUtils.getShipmentInputModelByAPI(shipmentModel, "getSortedShipmentDetails");
					
					j++;
					
					inputModel.MultiApi.API[j] = {};
					inputModel.MultiApi.API[j].Name = "changeShipment";
					inputModel.MultiApi.API[j].Input = {};
					inputModel.MultiApi.API[j].Input = backroomPickUtils.getShipmentInputModelByAPI(shipmentModel, "changeShipment");
					
					j++;
					
				}
				
				inputModel.MultiApi.API[j] = {};
				inputModel.MultiApi.API[j].Name = "getItemUOMMasterList";
				inputModel.MultiApi.API[j].Input = {};
				
				var itemUOMListAPIInputModel = {};
				itemUOMListAPIInputModel.ItemUOMMaster = {};
				itemUOMListAPIInputModel.ItemUOMMaster.EnterpriseCode = shipmentModel.EnterpriseCode;
				inputModel.MultiApi.API[j].Input = itemUOMListAPIInputModel;
				
				j++;
				
				inputModel.MultiApi.API[j] = {};
				inputModel.MultiApi.API[j].Name = "getUserHierarchy";
				inputModel.MultiApi.API[j].Input = {};
				
				var getUserHierAPIInputModel = {};
				getUserHierAPIInputModel.User = {};
				getUserHierAPIInputModel.User.Loginid = scUserprefs.getUserId();
				inputModel.MultiApi.API[j].Input = getUserHierAPIInputModel;
			
			}

			//console.log("multiApi : ",inputModel);
			
			return inputModel;
		
		};
		
		backroomPickUtils.getShipmentInputModelByAPI = function(shipmentModel, apiName) {
		
			var inputModel = {};
			inputModel.Shipment = {};
			
			inputModel.Shipment.ShipmentKey = shipmentModel.ShipmentKey;
			
			if(scBaseUtils.equals("changeShipment",apiName)) {
		
				inputModel.Shipment.PickTicketPrinted="Y";
				inputModel.Shipment.OverrideModificationRules="Y";				
			} 
			
			return inputModel;
			
		};

		backroomPickUtils.addHoldLocation = function(location,model,screen,namespace){
		
			var holdLocation = {};
			holdLocation.Location = location;
			holdLocation.toDelete = "N";
			//console.log(model);
			model = {};
			model.HoldLocationList={};
			model.HoldLocationList.HoldLocation= [];		
			model.HoldLocationList.HoldLocation[model.HoldLocationList.HoldLocation]=holdLocation;
			
			var childScreen = scScreenUtils.getChildScreen(screen, "HoldLocationListRef");
				//console.log("childScreen : ",childScreen);
				if(scBaseUtils.isVoid(childScreen)) {
					var options = scBaseUtils.getNewBeanInstance();
					scScreenUtils.showChildScreen(screen,"HoldLocationListRef","","",options,model);
					childScreen = scScreenUtils.getChildScreen(screen, "HoldLocationListRef");
					//console.log("childScreen : ",childScreen);
				} 
				childScreen.setModel(namespace,model,"");
				scEventUtils.fireEventToChild(screen, "HoldLocationListRef", "reloadScreen", null);
			
			
		};
		
		backroomPickUtils.getHoldLocationList = function(concatString){
			var holdLocationList = {};
			holdLocationList.HoldLocationList={};
			holdLocationList.HoldLocationList.HoldLocation=[];
			if(!scBaseUtils.isVoid(concatString)){				
				var splitStr = concatString.split(", ");
				for(i=0;i<splitStr.length;i++){
					holdLocationList.HoldLocationList.HoldLocation[i]={};
					holdLocationList.HoldLocationList.HoldLocation[i].Location=splitStr[i];
					holdLocationList.HoldLocationList.HoldLocation[i].toDelete="N";			
				}
			}		
			return holdLocationList;
		};
		
		backroomPickUtils.getHoldLocationUpdateModel = function(holdLocationTargetModel,shipmentLinePickedModel){
			var holdLocationUpdateModel = {};
			holdLocationUpdateModel.Shipment={};
			holdLocationUpdateModel.Shipment.ShipmentKey=shipmentLinePickedModel.Shipment.ShipmentKey;
			var holdLocationStr = "";
			for(i=0;i<holdLocationTargetModel.HoldLocation.length;i++){
				if(i==0){
					holdLocationStr+= holdLocationTargetModel.HoldLocation[i].Location;
				}
				else{
					holdLocationStr+= ", "+holdLocationTargetModel.HoldLocation[i].Location;
				}
			}
			holdLocationUpdateModel.Shipment.HoldLocation=holdLocationStr;
			return holdLocationUpdateModel;
		};
		
		backroomPickUtils.validateHoldLocation = function(holdLocationUpdateModel){
			
			if(holdLocationUpdateModel.Shipment.HoldLocation.length<=40){
				return true;
			}
			else{
				return false;
			}
		
		};
		
		backroomPickUtils.isHoldLocationDuplicate = function(location,holdLocationTargetModel){
			var isDuplicate = false;
			if(!scBaseUtils.isVoid(holdLocationTargetModel.HoldLocation)){
				for(i=0;i<holdLocationTargetModel.HoldLocation.length;i++){
					if(location==holdLocationTargetModel.HoldLocation[i].Location){
						isDuplicate = true;
						break;
					}				
				}	
				
			}
					
			return isDuplicate;
		};
		
		
		backroomPickUtils.appendHoldLocation = function(location,holdLocationTargetModel){
			var holdLocation = {};
			holdLocation.Location = location;
			holdLocation.toDelete = "N";
			var inputmodel = scBaseUtils.cloneModel(holdLocationTargetModel);
			if(scBaseUtils.isVoid(inputmodel)){
				inputmodel = {};
				inputmodel.HoldLocation=[];
			}			
			inputmodel.HoldLocation[inputmodel.HoldLocation.length]=holdLocation;
			
			return inputmodel;
		};
		
		
		backroomPickUtils.deleteHoldLocation=function(holdLocationTargetModel){
			var holdLocationModel = {};
			var holdLocationList = {};
			holdLocationList.HoldLocation=[];
			for (i=0;i<holdLocationTargetModel.HoldLocation.length;i++){
				if(holdLocationTargetModel.HoldLocation[i].toDelete!="Y"){
					var holdLocation = {};
					holdLocation.Location = holdLocationTargetModel.HoldLocation[i].Location;
					holdLocation.toDelete = "N";
					holdLocationList.HoldLocation[holdLocationList.HoldLocation.length]=holdLocation;
				}				
			}

			return holdLocationList;
		};
		
		backroomPickUtils.repaintChildScreen = function(screen,childid,model,namespace){
			var options = scBaseUtils.getNewBeanInstance();
			var childScreen = scScreenUtils.getChildScreen(screen, childid);
			//console.log("childScreen : ",childScreen);
			if(!scBaseUtils.isVoid(childScreen)) {
				scScreenUtils.destroyDynamicScreen(screen,childid);
			}
			if(!scBaseUtils.isVoid(model.HoldLocationList.HoldLocation) && model.HoldLocationList.HoldLocation.length>0){
				scScreenUtils.showChildScreen(screen,childid,"","",options,null);
				childScreen = scScreenUtils.getChildScreen(screen, childid);
				childScreen.setModel(namespace,model,"");
			}
		};
		
		backroomPickUtils.getTextOkObjectForMessageBox = function(screen) {
							
			var textObj = scBaseUtils.getNewBeanInstance();
			var textOK = scScreenUtils.getString(screen,"Ok");
			scBaseUtils.addStringValueToBean("OK",textOK,textObj);
			
			return textObj;
		
		};

		backroomPickUtils.validateScannedQuantity = function(screen, targetModel,statusImagePanel,shortageImagePanel,shortageResolutionIcon,shortageReasonFilterSelect) {
			
			//console.log("validateScannedQuantity");			
			//console.log("target Model : ",targetModel);
			var status = false;
			var saveStatus = false;
			var hideStatus = false;
			var isGreaterThan=false;
			if(!scBaseUtils.isVoid(targetModel) && !scBaseUtils.isVoid(targetModel.PickedQty)) {
				var shortageQty = scBaseUtils.isVoid(targetModel.ShortageQty)?Number("0"):Number(targetModel.ShortageQty);
			
				if(scBaseUtils.numberGreaterThan(Number(targetModel.PickedQty),Number(targetModel.Quantity)+shortageQty)){
					status = false;		
					isGreaterThan=true;
					saveStatus = false;
					hideStatus = false;
					scScreenUtils.showErrorMessageBox(screen,scScreenUtils.getString(screen,"ItemOverrage_BackroomPick"),"",backroomPickUtils.getTextOkObjectForMessageBox(screen),"");
				} else if(scBaseUtils.numberEquals(Number(targetModel.PickedQty),Number(targetModel.Quantity)+shortageQty)) {
					status = true;
					hideStatus = true;					
					saveStatus = true;
				} else if(scBaseUtils.numberLessThan(Number(targetModel.PickedQty),Number(targetModel.Quantity)+shortageQty)) {
					status = false;					
					saveStatus = true;
				} else if (scBaseUtils.numberEquals(Number("0"),Number(targetModel.PickedQty))){
					status = false;					
					saveStatus = true;
				}
			}
			if(!scBaseUtils.isVoid(targetModel) && scBaseUtils.isVoid(targetModel.PickedQty)) {
				status = false;						
				saveStatus = false;				
			}
			this.showHideWidgetsOnValidate(hideStatus,screen,statusImagePanel,shortageImagePanel,shortageResolutionIcon,shortageReasonFilterSelect);
			
			if(isGreaterThan){
				scWidgetUtils.hideWidget(screen,shortageReasonFilterSelect,false);
				scWidgetUtils.hideWidget(screen,shortageResolutionIcon,false);
			}
			
			return saveStatus;
			
		};
		
		backroomPickUtils.validatePickedQuantity = function(screen,shipmentLineModel,statusImagePanel,statusLabel,shortageImagePanel,shortageResolutionIcon,shortageReasonFilterSelect) {
			var status = false;
			if(!scBaseUtils.isVoid(shipmentLineModel.ShipmentLine.BackroomPickedQuantity) && !scBaseUtils.isVoid(shipmentLineModel.ShipmentLine.Quantity)) {
				
				// changes for handling line previously marked as shortage
				var shortageQty = scBaseUtils.isVoid(shipmentLineModel.ShipmentLine.ShortageQty)?Number("0"):Number(shipmentLineModel.ShipmentLine.ShortageQty);
				if(scBaseUtils.numberEquals(Number(shipmentLineModel.ShipmentLine.BackroomPickedQuantity),Number(shipmentLineModel.ShipmentLine.Quantity)) && scBaseUtils.numberEquals(shortageQty, 0)) {
					status = true;
				}
			
			}
			this.showHideWidgetsOnValidate(status,screen,statusImagePanel,statusLabel,shortageImagePanel,shortageResolutionIcon,shortageReasonFilterSelect);
						
		};
		
		backroomPickUtils.showHideWidgetsOnValidate = function(status,screen,statusImagePanel,statusLabel, shortageImagePanel,shortageResolutionIcon,shortageReasonFilterSelect){
			scWidgetUtils.removeClass(screen,"shipmentLineDetailsContainer","completedLine");
			scWidgetUtils.removeClass(screen,"shipmentLineDetailsContainer","shortedLine");		
			if(status){
				scWidgetUtils.showWidget(screen,statusImagePanel,false,"");	
				scWidgetUtils.setLabelText(screen,statusLabel,scScreenUtils.getString(screen,"Label_Picked"),false);	
				scWidgetUtils.hideWidget(screen,shortageImagePanel,false);
				scWidgetUtils.hideWidget(screen,shortageResolutionIcon,false);
				scWidgetUtils.hideWidget(screen,shortageReasonFilterSelect,false);
				scWidgetUtils.addClass(screen,"shipmentLineDetailsContainer","completedLine");
			}
			else{
				scWidgetUtils.hideWidget(screen,statusImagePanel,false);
				scWidgetUtils.setLabelText(screen,statusLabel,scScreenUtils.getString(screen,"Label_emptyLabel"),false);	
				scWidgetUtils.showWidget(screen,shortageReasonFilterSelect,false,"");
				scWidgetUtils.removeClass(screen,"shipmentLineDetailsContainer","completedLine");
				if(screen.screenMode=="ItemScanMobile" && screen.shortageResolved){
					scWidgetUtils.showWidget(screen,shortageImagePanel,false,"");
					scWidgetUtils.hideWidget(screen,shortageResolutionIcon,false);
					scWidgetUtils.addClass(screen,"shipmentLineDetailsContainer","shortedLine");		
				}
				else if (screen.screenMode=="ItemScanMobile" && !screen.shortageResolved){
					scWidgetUtils.showWidget(screen,shortageResolutionIcon,false,"");
					scWidgetUtils.hideWidget(screen,shortageImagePanel,false);
					scWidgetUtils.removeClass(screen,"shipmentLineDetailsContainer","shortedLine");		
				}
				else if(screen.screenMode=="ItemScan"){
					scWidgetUtils.showWidget(screen,shortageReasonFilterSelect,false,"");
				}
			}
			if(screen.screenMode=="ReadOnlyMobile"){
				scWidgetUtils.hideWidget(screen,statusImagePanel,false);
				scWidgetUtils.hideWidget(screen,shortageImagePanel,false);
				scWidgetUtils.hideWidget(screen,shortageResolutionIcon,false);
				scWidgetUtils.removeClass(screen,"shipmentLineDetailsContainer","completedLine");
				scWidgetUtils.removeClass(screen,"shipmentLineDetailsContainer","shortedLine");		
			}
			else if(screen.screenMode=="ItemScan"){				
				scWidgetUtils.hideWidget(screen,shortageImagePanel,false);
				scWidgetUtils.hideWidget(screen,shortageResolutionIcon,false);
				scWidgetUtils.removeClass(screen,"shipmentLineDetailsContainer","shortedLine");		
				if(screen.shortageResolved){
					scWidgetUtils.addClass(screen,"shipmentLineDetailsContainer","shortedLine");		
				}
			}			
		};
		
		backroomPickUtils.processScannedProduct = function(screen,targetModel) {

			var PickAll_Input = {};
			var pickedQty=Number("0");
			
			if(scBaseUtils.isVoid(targetModel.Quantity)) {
				pickedQty=Number("0");
			} else {
				pickedQty=Number(targetModel.Quantity);
			}
					PickAll_Input.Quantity = Number(pickedQty) + Number("1");
			//screen.setModel("PickAll_Input", PickAll_Input, "");
			return PickAll_Input;
		};
		
		backroomPickUtils.processBackroomPickedScannedQuantity = function(scannedItemQty,shipmentLineModel) {

			var pickedQty=Number("0");
			
			if(scBaseUtils.isVoid(shipmentLineModel.ShipmentLine.BackroomPickedQuantity) || scBaseUtils.numberEquals(Number(shipmentLineModel.ShipmentLine.BackroomPickedQuantity),Number("0"))) {
				pickedQty=Number("0");
			} else {
				pickedQty=Number(shipmentLineModel.ShipmentLine.BackroomPickedQuantity);
			}
			pickedQty = Number(pickedQty) + Number(scannedItemQty);
			//screen.setModel("PickAll_Input", PickAll_Input, "");
			return pickedQty;
		};

		backroomPickUtils.hasShortages = function(pickedShipmentModel){
			var hasShortages = false;
		
			if(!scBaseUtils.isVoid(pickedShipmentModel) && !scBaseUtils.isVoid(pickedShipmentModel.Shipment.ShipmentLines.ShipmentLine)){
				var pickedLines = pickedShipmentModel.Shipment.ShipmentLines.ShipmentLine;
				for(i=0;i<pickedLines.length;i++){					
					var pickedQty = !scBaseUtils.isVoid(pickedLines[i].BackroomPickedQuantity)?Number(pickedLines[i].BackroomPickedQuantity):Number('0');
					var qty = Number(pickedLines[i].Quantity);
					if(pickedQty<qty && scBaseUtils.isVoid(pickedLines[i].ShortageReason)){
						hasShortages = true;
						break;
					}
				}
			}
			
			return hasShortages;
		};
		
		backroomPickUtils.hasOverages = function(pickedShipmentModel){
			var hasOverages = false;
		
			if(!scBaseUtils.isVoid(pickedShipmentModel) && !scBaseUtils.isVoid(pickedShipmentModel.Shipment.ShipmentLines.ShipmentLine)){
				var pickedLines = pickedShipmentModel.Shipment.ShipmentLines.ShipmentLine;
				for(i=0;i<pickedLines.length;i++){					
					var pickedQty = !scBaseUtils.isVoid(pickedLines[i].BackroomPickedQuantity)?Number(pickedLines[i].BackroomPickedQuantity):Number('0');
					var qty = Number(pickedLines[i].Quantity);
					if(pickedQty>qty){
						hasOverages = true;
						break;
					}
				}
			}
			
			return hasOverages;
		};
		
		backroomPickUtils.getShortageReasonList = function(){		
			var resolutionType={
				"ShortageReason":[
					{
						"ShortageReasonCode":"AllInventoryShortage",
						"ShortageReasonDesc":""+scBundleUtils.getString('AllInventoryShortage')
					}
				]
			};
			return resolutionType;
		};
		
		backroomPickUtils.getPickedAllChangeShipmentModel = function(pickedShipmentModel){
			var pickedAllModel = {};
			pickedAllModel.Shipment={};
			pickedAllModel.Shipment.ShipmentKey=pickedShipmentModel.Shipment.ShipmentKey;
			pickedAllModel.Shipment.ShipmentLines={};
			pickedAllModel.Shipment.ShipmentLines.ShipmentLine=[];
		
			if(!scBaseUtils.isVoid(pickedShipmentModel.Shipment.ShipmentLines.ShipmentLine)){
				var pickedLines = pickedShipmentModel.Shipment.ShipmentLines.ShipmentLine;		
				
				for(i=0;i<pickedLines.length;i++){					
					var pickedLine = {};
					pickedLine.ShipmentLineKey=pickedLines[i].ShipmentLineKey;
					pickedLine.BackroomPickedQuantity=pickedLines[i].Quantity;
					pickedAllModel.Shipment.ShipmentLines.ShipmentLine[i]=pickedLine;
				}
			}
			
			return pickedAllModel;
		};
		
		backroomPickUtils.updateShipmentLineForShortage = function(shipmentLineModel){
			
			var format = "formattedQty";
			var model = scBaseUtils.cloneModel(shipmentLineModel);
			if(scBaseUtils.isVoid(model.ShipmentLine.BackroomPickedQuantity)){
				model.ShipmentLine.BackroomPickedQuantity = 0;
			}
			model.ShipmentLine.ShortageQty = Number(model.ShipmentLine.Quantity)-Number(model.ShipmentLine.BackroomPickedQuantity);
			model.ShipmentLine.ShortageQty = iasUOMUtils.getFormatedQuantityWithUom(""+model.ShipmentLine.ShortageQty,'',format);
			//model.ShipmentLine.Quantity = Number(model.ShipmentLine.BackroomPickedQuantity);
			model.ShipmentLine.Quantity = iasUOMUtils.getFormatedQuantityWithUom(""+model.ShipmentLine.BackroomPickedQuantity,'',format);
			return model;
		};
		
		backroomPickUtils.getPickedChangeShipmentModel = function(pickedShipmentLineModel){
			var pickedModel = {};
			pickedModel.Shipment={};
			pickedModel.Shipment.ShipmentKey=pickedShipmentLineModel.ShipmentLine.ShipmentKey;
			pickedModel.Shipment.ShipmentLines={};
			pickedModel.Shipment.ShipmentLines.ShipmentLine=[];			
							
			var pickedLine = {};
			pickedLine.ShipmentLineKey=pickedShipmentLineModel.ShipmentLine.ShipmentLineKey;
			var pickedQty = !scBaseUtils.isVoid(pickedShipmentLineModel.ShipmentLine.BackroomPickedQuantity)?Number(pickedShipmentLineModel.ShipmentLine.BackroomPickedQuantity):Number('0');
			pickedLine.BackroomPickedQuantity=pickedQty;
			pickedModel.Shipment.ShipmentLines.ShipmentLine[0]=pickedLine;			
			
			return pickedModel;
		};
		
		backroomPickUtils.getShortageChangeShipmentModel = function(pickedShipmentLineModel,args){
			var pickedModel = {};
			pickedModel.Shipment={};
			pickedModel.Shipment.ShipmentKey=pickedShipmentLineModel.ShipmentLine.ShipmentKey;
			pickedModel.Shipment.ShipmentLines={};
			pickedModel.Shipment.ShipmentLines.ShipmentLine=[];			
							
			var pickedLine = {};
			pickedLine.ShipmentLineKey=pickedShipmentLineModel.ShipmentLine.ShipmentLineKey;
			var pickedQty = !scBaseUtils.isVoid(pickedShipmentLineModel.ShipmentLine.BackroomPickedQuantity)?Number(pickedShipmentLineModel.ShipmentLine.BackroomPickedQuantity):Number('0');
			var shortageQty = !scBaseUtils.isVoid(pickedShipmentLineModel.ShipmentLine.ShortageQty)?Number(pickedShipmentLineModel.ShipmentLine.ShortageQty):Number('0');
			pickedLine.BackroomPickedQuantity=pickedQty;	
			if(!scBaseUtils.isVoid(args) && backroomPickUtils.isLineMarkedWithInventoryShortage(args.ShortedShipmentLine)) {
				pickedLine.Quantity=pickedQty;
			} else {
				pickedLine.Quantity=pickedShipmentLineModel.ShipmentLine.Quantity;
			}
			
			pickedLine.ShortageQty=(Number(pickedShipmentLineModel.ShipmentLine.Quantity)-pickedQty) + shortageQty ;	
			pickedModel.Shipment.ShipmentLines.ShipmentLine[0]=pickedLine;	

			/*if(args.isLastShortageLineForCancellingShipment=="true" || args.isLastShortageLineForCancellingShipment==true){
				pickedModel.Shipment.Action="Cancel";
			}*/			
			return pickedModel;
		};
		
		
		backroomPickUtils.isLineMarkedWithInventoryShortage = function(shortedShipmentModel){
			
			if(!scBaseUtils.isVoid(shortedShipmentModel) && !scBaseUtils.isVoid(shortedShipmentModel.ShipmentLine) && scBaseUtils.equals("AllInventoryShortage",shortedShipmentModel.ShipmentLine.ShortageReasonCode)) {
				return true;
			}
			
			return false;
			
		};
		
		
		
		backroomPickUtils.hasAnyLinePicked = function(pickedShipmentModel){
			var totalPickedQty = 0;
			
			if(!scBaseUtils.isVoid(pickedShipmentModel.Shipment.ShipmentLines.ShipmentLine)){
				var pickedLines = pickedShipmentModel.Shipment.ShipmentLines.ShipmentLine;		
				
				for(i=0;i<pickedLines.length;i++){	
					
					var pickedQty = !scBaseUtils.isVoid(pickedLines[i].BackroomPickedQuantity)?Number(pickedLines[i].BackroomPickedQuantity):Number('0');
					
					totalPickedQty+=pickedQty;
				}
			}
			
			return totalPickedQty>0?true:false;
		};
		
		backroomPickUtils.getChangeShipmentModel = function(pickedShipmentModel){
			var changeShipmentModel = {};
			changeShipmentModel.Shipment={};
			changeShipmentModel.Shipment.ShipmentKey=pickedShipmentModel.Shipment.ShipmentKey;
			changeShipmentModel.Shipment.ShipmentLines={};
			changeShipmentModel.Shipment.ShipmentLines.ShipmentLine=[];
			if(!scBaseUtils.isVoid(pickedShipmentModel.Shipment.ShipmentLines.ShipmentLine)){
				var pickedLines = pickedShipmentModel.Shipment.ShipmentLines.ShipmentLine;		
				
				for(i=0;i<pickedLines.length;i++){					
					var pickedLine = {};
					pickedLine.ShipmentLineKey=pickedLines[i].ShipmentLineKey;
					var pickedQty = !scBaseUtils.isVoid(pickedLines[i].BackroomPickedQuantity)?Number(pickedLines[i].BackroomPickedQuantity):Number('0');
					pickedLine.BackroomPickedQuantity=pickedQty;
					pickedLine.Quantity=pickedQty;
					if(pickedQty<Number(pickedLines[i].Quantity)){
						pickedLine.ShortageQty=Number(pickedLines[i].Quantity) - pickedQty;
					}					
					changeShipmentModel.Shipment.ShipmentLines.ShipmentLine[i]=pickedLine;
				}
			}
			if(!this.hasAnyLinePicked(pickedShipmentModel)){
				changeShipmentModel.Shipment.Action="Cancel";
			}
			
			return changeShipmentModel;
		};
		
		backroomPickUtils.getChangeShipmentStatusModel = function(pickedShipmentModel){
			var changeShipmentStatusModel = {};
			changeShipmentStatusModel.Shipment={};
			changeShipmentStatusModel.Shipment.ShipmentKey=pickedShipmentModel.Shipment.ShipmentKey;
			return changeShipmentStatusModel;
		};
		
		backroomPickUtils.getNotesForShortageLines = function(pickedShipmentModel){
			/*
				<MultiApi>
					<API Name="changeOrder">           
						<Input>
							<Order
								DisplayLocalizedFieldInLocale="xml:CurrentUser:/User/@Localecode" OrderHeaderKey="">
								<OrderLines>
									<OrderLine Action="MODIFY" OrderLineKey="">
										<Notes>
											<Note NoteText="Shortage occurred while picking from backroom. The shortage resolution was recorded as: Inventory shortage " ReasonCode="YCD_BACKROOM_PICK_SHORTAGE"/>
										</Notes>
									</OrderLine>
								</OrderLines>
							</Order>
						</Input>
						<Template/>
					</API>
				</MultiApi>		
			
			*/
			var multiApiInput = {};			
			var pickedLines = pickedShipmentModel.Shipment.ShipmentLines.ShipmentLine;
			var orderList = [];
			for ( i=0;i<pickedLines.length;i++){
				var orderFound=false;
				for(j=0;j<orderList.length;j++){					
					if(!scBaseUtils.isVoid(orderList[j].Input.Order) && orderList[j].Input.Order.OrderHeaderKey == pickedLines[i].OrderHeaderKey){
						var pickedQty = !scBaseUtils.isVoid(pickedLines[i].BackroomPickedQuantity)?Number(pickedLines[i].BackroomPickedQuantity):Number('0');
						if(pickedQty<Number(pickedLines[i].Quantity)){
							var orderline={};
							orderline.OrderLineKey=pickedLines[i].OrderLineKey;
							orderline.Notes={};
							orderline.Notes.Note=[];
							orderline.Notes.Note[0]={};							
							orderline.Notes.Note[0].NoteText=scBundleUtils.getString("Inventory_Shortage_OrderLine_Note");
							orderList[j].Input.Order.OrderLines.OrderLine[orderList[j].Input.Order.OrderLines.OrderLine.length]=orderline;	
						}
						orderFound=true;
						break;
					}			
				}
				if(!orderFound){
					var pickedQty = !scBaseUtils.isVoid(pickedLines[i].BackroomPickedQuantity)?Number(pickedLines[i].BackroomPickedQuantity):Number('0');
					if(pickedQty<Number(pickedLines[i].Quantity)){			
						var order = {};
						order.OrderHeaderKey=pickedLines[i].OrderHeaderKey;					
						order.OrderLines={};
						order.OrderLines.OrderLine=[];
						order.OrderLines.OrderLine[0]={};
						order.OrderLines.OrderLine[0].OrderLineKey=pickedLines[i].OrderLineKey;
						order.OrderLines.OrderLine[0].Notes={};
						order.OrderLines.OrderLine[0].Notes.Note=[];
						order.OrderLines.OrderLine[0].Notes.Note[0]={};
						order.OrderLines.OrderLine[0].Notes.Note[0].NoteText=scBundleUtils.getString("Inventory_Shortage_OrderLine_Note");
						var input={};
						input.Input={};
						input.Input.Order=order;
						orderList[orderList.length] = input;
					}
				}		
			}
			multiApiInput.MultiApi = {};
			multiApiInput.MultiApi.API=orderList;			
			return multiApiInput;
		};
		
		
		backroomPickUtils.getNotesForShortageShipmentLine = function(pickedShipmentLineModel){
			/*
				
				<Order
					DisplayLocalizedFieldInLocale="xml:CurrentUser:/User/@Localecode" OrderHeaderKey="">
					<OrderLines>
						<OrderLine Action="MODIFY" OrderLineKey="">
							<Notes>
								<Note NoteText="Shortage occurred while picking from backroom. The shortage resolution was recorded as: Inventory shortage " ReasonCode="YCD_BACKROOM_PICK_SHORTAGE"/>
							</Notes>
						</OrderLine>
					</OrderLines>
				</Order>
						
			
			*/
			
			var pickedLine = pickedShipmentLineModel.ShipmentLine;
			var orderNotes = {};
			var order = {};
			order.OrderHeaderKey=pickedLine.OrderHeaderKey;					
			order.OrderLines={};
			order.OrderLines.OrderLine=[];
			order.OrderLines.OrderLine[0]={};
			order.OrderLines.OrderLine[0].OrderLineKey=pickedLine.OrderLineKey;
			order.OrderLines.OrderLine[0].Notes={};
			order.OrderLines.OrderLine[0].Notes.Note=[];
			order.OrderLines.OrderLine[0].Notes.Note[0]={};
			order.OrderLines.OrderLine[0].Notes.Note[0].NoteText=scBundleUtils.getString("Inventory_Shortage_OrderLine_Note");
							
			orderNotes.Order = order;
			
			return orderNotes;
		};
		
		backroomPickUtils.getInvokeUEModel = function(pickedShipmentModel){
			var invokeUEModel = {};
			
			
			/*<InvokeUE
								DisplayLocalizedFieldInLocale="xml:CurrentUser:/User/@Localecode"
								EnterpriseCode="xml:CurrentStore:/Store/@EnterpriseCode"
								TransactionId="COM_PCA_USER_EXIT" UserExit="com.yantra.pca.ycd.japi.ue.YCDUpdateLocationInventoryUE">
								<XMLData>
									<UpdateLocationInventory
										EnterpriseCode="xml:CurrentStore:/Store/@EnterpriseCode" Node="xml:CurrentStore:/Store/@ShipNode">
										<InventoryList>
											<Inventory Quantity="">
												<InventoryItem ItemID="" ProductClass="" UnitOfMeasure=""/>
											</Inventory>
										</InventoryList>
									</UpdateLocationInventory>
								</XMLData>
							</InvokeUE>*/
			invokeUEModel.InvokeUE={};
			invokeUEModel.InvokeUE.XMLData={};
			invokeUEModel.InvokeUE.XMLData.UpdateLocationInventory={};
			invokeUEModel.InvokeUE.XMLData.UpdateLocationInventory.InventoryList={};
			invokeUEModel.InvokeUE.XMLData.UpdateLocationInventory.InventoryList.Inventory=[];
			if(!scBaseUtils.isVoid(pickedShipmentModel.Shipment.ShipmentLines.ShipmentLine)){
				var pickedLines = pickedShipmentModel.Shipment.ShipmentLines.ShipmentLine;		
				
				for(i=0;i<pickedLines.length;i++){
					var inventory = {};					
					var pickedQty = !scBaseUtils.isVoid(pickedLines[i].BackroomPickedQuantity)?Number(pickedLines[i].BackroomPickedQuantity):Number('0');
					if(pickedQty>0){
						inventory.Quantity=pickedQty;
						inventory.InventoryItem={};
						inventory.InventoryItem={};
						inventory.InventoryItem.ItemID=pickedLines[i].OrderLine.ItemDetails.ItemID;
						if(scBaseUtils.isVoid(pickedLines[i].OrderLine.Item) || scBaseUtils.isVoid(pickedLines[i].OrderLine.ItemProductClass)){
							inventory.InventoryItem.ProductClass="";
						}
						else{
							inventory.InventoryItem.ProductClass=pickedLines[i].OrderLine.Item.ProductClass;
						}
						inventory.InventoryItem.UnitOfMeasure=pickedLines[i].OrderLine.ItemDetails.UnitOfMeasure;
						
						var l = invokeUEModel.InvokeUE.XMLData.UpdateLocationInventory.InventoryList.Inventory.length;
						invokeUEModel.InvokeUE.XMLData.UpdateLocationInventory.InventoryList.Inventory[l]=inventory;
					}
				}
			}		
			return invokeUEModel;
		};
		
		backroomPickUtils.getInvokeUEModelForShipmentLine = function(pickedShipmentLineModel){
			var invokeUEModel = {};
			
			invokeUEModel.InvokeUE={};
			invokeUEModel.InvokeUE.XMLData={};
			invokeUEModel.InvokeUE.XMLData.UpdateLocationInventory={};
			invokeUEModel.InvokeUE.XMLData.UpdateLocationInventory.InventoryList={};
			invokeUEModel.InvokeUE.XMLData.UpdateLocationInventory.InventoryList.Inventory=[];
			if(!scBaseUtils.isVoid(pickedShipmentLineModel.ShipmentLine)){
				var pickedLines = pickedShipmentLineModel.ShipmentLine;				
				
					var inventory = {};					
					var pickedQty = !scBaseUtils.isVoid(pickedLines.BackroomPickedQuantity)?Number(pickedLines.BackroomPickedQuantity):Number('0');
					
					inventory.Quantity=pickedQty;
					inventory.InventoryItem={};
					inventory.InventoryItem={};
					inventory.InventoryItem.ItemID=pickedLines.OrderLine.ItemDetails.ItemID;
					if(scBaseUtils.isVoid(pickedLines.OrderLine.Item) || scBaseUtils.isVoid(pickedLines.OrderLine.ItemProductClass)){
						inventory.InventoryItem.ProductClass="";
					}
					else{
						inventory.InventoryItem.ProductClass=pickedLines.OrderLine.Item.ProductClass;
					}
					inventory.InventoryItem.UnitOfMeasure=pickedLines.OrderLine.ItemDetails.UnitOfMeasure;					
					invokeUEModel.InvokeUE.XMLData.UpdateLocationInventory.InventoryList.Inventory[0]=inventory;
							
			}		
			return invokeUEModel;		
		};
		
		backroomPickUtils.getChangeShipmentQtyStatusNotesModel = function(pickedShipmentModel){
		
			
			var changeShipmentStatusModel = this.getChangeShipmentStatusModel(pickedShipmentModel);
			var changeShipmentModel = this.getChangeShipmentModel(pickedShipmentModel);
			var addNotesModel = this.getNotesForShortageLines(pickedShipmentModel);
			var invokeUEModel = this.getInvokeUEModel(pickedShipmentModel);
			
			
			var multiApiModel = {};
			multiApiModel.MultiApi = {};
			multiApiModel.MultiApi.API=[];
			multiApiModel.MultiApi.API[0]={};
			multiApiModel.MultiApi.API[0].Name="changeShipment";
			multiApiModel.MultiApi.API[0].Input={};
			multiApiModel.MultiApi.API[0].Input.Shipment=changeShipmentStatusModel.Shipment;
			multiApiModel.MultiApi.API[1]={};
			multiApiModel.MultiApi.API[1].Name="invokeUE";
			multiApiModel.MultiApi.API[1].Input={};
			multiApiModel.MultiApi.API[1].Input.InvokeUE=invokeUEModel.InvokeUE;
			multiApiModel.MultiApi.API[2]={};
			multiApiModel.MultiApi.API[2].Name="changeShipmentStatus";
			multiApiModel.MultiApi.API[2].Input={};
			multiApiModel.MultiApi.API[2].Input.Shipment=changeShipmentStatusModel.Shipment;
			multiApiModel.MultiApi.API[3]={};
			multiApiModel.MultiApi.API[3].Name="changeOrder";
			multiApiModel.MultiApi.API[3].Input={};
			multiApiModel.MultiApi.API[3].Input.InvokeUE=addNotesModel.Order;			
			return multiApiModel;
		};
		
		backroomPickUtils.getBackroomPickShipmentDisplayModel = function(model){
			var shipmentModelDisplay = scBaseUtils.cloneModel(model);
			var shipmentModel = shipmentModelDisplay.Shipment;
			if(!scBaseUtils.isVoid(shipmentModel) && !scBaseUtils.isVoid(shipmentModel.ShipmentLines) && !scBaseUtils.isVoid(shipmentModel.ShipmentLines.ShipmentLine) && !scBaseUtils.isVoid(shipmentModel.ShipmentLines.ShipmentLine.length)) {
					//var shipmentLinesModel = [];
					for(var j=0;j<shipmentModel.ShipmentLines.ShipmentLine.length;j++) {
						var shipmentLineModel = shipmentModel.ShipmentLines.ShipmentLine[j];
						
						if("Y" == shipmentLineModel.OrderLine.IsBundleParent || scBaseUtils.equals(Number("0"), Number(shipmentLineModel.Quantity))) {
							shipmentModel.ShipmentLines.ShipmentLine.splice(j,1);
							j--;
							continue;
						}					
						else{
							//shipmentLineModel[shipmentLineModel.length]=shipmentLineModel;
					}				
				}
					//shipmentModelDisplay.ShipmentLines.ShipmentLine=shipmentLineModel;
				}
			return shipmentModelDisplay;
		};
		
		backroomPickUtils.updateShipmentLineContainer = function(screen, productScanDetailModel) {
		
			//console.log("updateShipmentLineContainer");
			//console.log("productScanDetailModel : ",productScanDetailModel);
			
			var type = "wsc.components.shipment.common.screens.ShipmentLineDetails";
			var isOverragePresent = false, isProductFound= false;
			var lastProductScannedModel = {};
			lastProductScannedModel.ProductConsumed = "N";
			lastProductScannedModel.OrderLine = {};
			
			var PickAll_Input = {};
			var pickedQty=Number("0");
			var scannedItemQty=Number("0");
			var scannedItemID = "", scannedItemPresent = false;
			var shipmentLineKey= "";
			
			var barCodeDataModel = productScanDetailModel.BarCode;
			
			if(!scBaseUtils.isVoid(productScanDetailModel) && !scBaseUtils.isVoid(productScanDetailModel.BarCode) && !scBaseUtils.isVoid(productScanDetailModel.BarCode.Translations))  {
			
				if(scBaseUtils.equals("0",productScanDetailModel.BarCode.Translations.TotalNumberOfRecords)) {
					scScreenUtils.showWarningMessageBoxWithOk(screen,scScreenUtils.getString(screen,"NoProductFound"),"", "","");
					return;
				}
				
				scannedItemPresent = true;
				
			} else {
				
				scScreenUtils.showWarningMessageBoxWithOk(screen,scScreenUtils.getString(screen,"NoProductFound"),"", "","");
					return;
			
			}
			
			if(scannedItemPresent && !scBaseUtils.isVoid(productScanDetailModel.BarCode.Translations.Translation) && !scBaseUtils.isVoid(productScanDetailModel.BarCode.Translations.Translation.ItemContextualInfo) && !scBaseUtils.isVoid(productScanDetailModel.BarCode.Translations.Translation.ItemContextualInfo.ItemID)) {
				scannedItemID = productScanDetailModel.BarCode.Translations.Translation.ItemContextualInfo.ItemID;
				scannedItemQty = Number(productScanDetailModel.BarCode.Translations.Translation.ItemContextualInfo.Quantity);
				shipmentLineKey =  productScanDetailModel.BarCode.Translations.Translation.ShipmentContextualInfo.ShipmentLineKey;
			} else {
				scScreenUtils.showWarningMessageBoxWithOk(screen,scScreenUtils.getString(screen,"NoProductFound"),"", "","");
					return;
			}
			
			//console.log("scannedItemID : ",scannedItemID);
			
			var container = screen.getWidgetByUId("repeatingIdentifierScreen");
			//console.log("container : ",container);
			var listOfChildren = container.getChildren(container);
			//console.log("listOfChildren : ",listOfChildren);
			for (var i=0;i<listOfChildren.length;i++) {
				var child = listOfChildren[i];
				//console.log("Iteration : ",(i+1));
				if (iasUIUtils.instanceOf(child,type) || (child.screenId && child.screenId == type)) {
					if(child instanceof scScreen ){						
						var shipmentLineOutput = scScreenUtils.getTargetModel(child, "ShipmentLine_Output", null);										
						if(scBaseUtils.stringEquals(shipmentLineKey,shipmentLineOutput.ShipmentLine.ShipmentLineKey)) {
							isProductFound = true;
							shipmentLineOutput.ShipmentLine.ProductConsumed = "Y";
							shipmentLineOutput.ShipmentLine.screenUId = child.uId;
							shipmentLineOutput.ShipmentLine.BackroomPickedQuantity = this.processBackroomPickedScannedQuantity(scannedItemQty, shipmentLineOutput);
							
							var eventArgs = null;
				            var eventDefn = null;
				            eventDefn = scBaseUtils.getNewBeanInstance();
				            eventArgs = scBaseUtils.getNewBeanInstance();
				            scBaseUtils.setAttributeValue("inputData", shipmentLineOutput, eventArgs);
				            scBaseUtils.setAttributeValue("argumentList", eventArgs, eventDefn);
							scEventUtils.fireEventInsideScreen(child, "updateScannedProductQuantity", null,eventArgs);
							break;							
						}							
					}						
				}				
			}
			
			//console.log("isProductFound : ",isProductFound);
			//console.log("lastProductScannedModel : ",lastProductScannedModel);
			
			if(isProductFound && scBaseUtils.stringEquals("N",shipmentLineOutput.ShipmentLine.ProductConsumed)) {
				scScreenUtils.showWarningMessageBoxWithOk(child,scScreenUtils.getString(screen,"OverrageProductMessage"),"", "","");			
			} else if(!isProductFound) {
				scScreenUtils.showWarningMessageBoxWithOk(screen,scScreenUtils.getString(screen,"InvalidProductScanned"),"", "","");
			} else if(isProductFound && scBaseUtils.stringEquals("Y",shipmentLineOutput.ShipmentLine.ProductConsumed)) {
								
					var lastScannedModelInWizard = iasUIUtils.getWizardModel(screen,"lastScannedProduct");
					if(!scBaseUtils.isVoid(lastScannedModelInWizard)) {
						// Remove CSS for previously highlighted panel 
						scWidgetUtils.removeClass(screen,lastScannedModelInWizard.screenUId,"highlightRepeatingPanel");
					}
					iasUIUtils.setWizardModel(screen,"lastScannedProduct",shipmentLineOutput,null);
						// Add CSS to Highlight Last Product Scanned panel 
					scWidgetUtils.addClass(screen,shipmentLineOutput.ShipmentLine.screenUId,"highlightRepeatingPanel");
					//scScreenUtils.scrollToWidget(screen,lastProductScannedModel.screenUId);
					
					
					//console.log("Updating Last Product Scanned Model");
					scWidgetUtils.showWidget(screen,"lastScannedProductDetailPanel",false,null);
					var options = scBaseUtils.getNewBeanInstance();
					scBaseUtils.addBeanValueToBean("lastProductScannedModel",shipmentLineOutput,options);
					var childScreen = scScreenUtils.getChildScreen(screen, "lastProductScannedDetailsScreenRef");
					//console.log("childScreen : ",childScreen);
					screen.setModel("lastProductScanned_output",shipmentLineOutput,"");
					if(scBaseUtils.isVoid(childScreen)) {
						scScreenUtils.showChildScreen(screen,"lastProductScannedDetailsScreenRef","","",options,shipmentLineOutput);
					} else {
						//childScreen.setModel("lastProductScanned_output",shipmentLineOutput,"");
						scEventUtils.fireEventToChild(screen, "lastProductScannedDetailsScreenRef", "updateLastProductScanned", null);
					}
					
			}
			
		};	
		
		
		backroomPickUtils.getDisplayUOM = function(unformattedValue,screen,widget,namespace,model) {
		
			var displayUOM = unformattedValue;
			if(scBaseUtils.isVoid(displayUOM)) {
				displayUOM =  model.OrderLine.ItemDetails.UnitOfMeasure;
			} 
			//console.log("displayUOM : ",displayUOM);
			return displayUOM;
		
		};
		
		backroomPickUtils.getOfQty = function(unformattedValue,screen,widget,namespace,shipmentLine) {
			var dataArray = [];
			
			var uom="";
			var quantity=unformattedValue;
			var format="qtyOnly";
			uom = scModelUtils.getStringValueFromPath("DisplayUnitOfMeasure", shipmentLine);
			if(scBaseUtils.isVoid(quantity)){
				quantity = scModelUtils.getStringValueFromPath("ShipmentLine.Quantity", shipmentLine);
			}			
			var shortageQty = scModelUtils.getStringValueFromPath("ShipmentLine.ShortageQty", shipmentLine);
			shortageQty = scBaseUtils.isVoid(shortageQty)?Number("0"):Number(shortageQty);
			var qty = Number(quantity)+shortageQty;
			displayQtyUOM =  iasUOMUtils.getFormatedQuantityWithUom(qty, uom, format);
			dataArray[0] =  displayQtyUOM;			
			return scScreenUtils.getFormattedString(screen,"OfQty",dataArray);			
		};
		
		backroomPickUtils.buildCompleteInputGetShipmentList = function(shipmentModel, complexQryModel, orderByModel) {
			var completeModel = {};
			completeModel.Shipment = {};
			if(!scBaseUtils.isVoid(shipmentModel.Shipment)){
				completeModel.Shipment = shipmentModel.Shipment;
			}
			if(!scBaseUtils.isVoid(complexQryModel.ComplexQuery)){
				completeModel.Shipment.ComplexQuery = complexQryModel.ComplexQuery;
			}
			if(!scBaseUtils.isVoid(orderByModel.OrderBy)){
				completeModel.Shipment.OrderBy = orderByModel.OrderBy;
			}
			
			return completeModel;
		};
		
		backroomPickUtils.buildQryForShipmentList = function(filterOptionsModel) {
			
			var complexQryModel = {};
			complexQryModel.ComplexQuery={};
			complexQryModel.ComplexQuery.Operator="AND";
			complexQryModel.ComplexQuery.Or={};
			complexQryModel.ComplexQuery.Or.Exp=[];
			
			var pos = 0;
			var backroomPick;
			if(filterOptionsModel.Filter.BackroomPickup == "Y"){
				complexQryModel.ComplexQuery.Or.Exp[pos]=backroomPickUtils.buildQueryElement("Status", "1100.70.06.10","EQ");
				pos++;
			}
			if(filterOptionsModel.Filter.PicksInProgress == "Y"){
				complexQryModel.ComplexQuery.Or.Exp[pos]=backroomPickUtils.buildQueryElement("Status", "1100.70.06.20","EQ");
				pos++;
			}
			if(filterOptionsModel.Filter.CustomerPickup == "Y"){
				complexQryModel.ComplexQuery.Or.Exp[pos]=backroomPickUtils.buildQueryElement("Status", "1100.70.06.30","EQ");
				pos++;
			}
			if(filterOptionsModel.Filter.ShipmentComplete == "Y"){
				complexQryModel.ComplexQuery.Or.Exp[pos]=backroomPickUtils.buildQueryElement("Status", "1400","EQ");
				pos++;
			}
			//If no elements were added, blank out the return model
			if(pos == 0){
				complexQryModel={};
			}
			
			return complexQryModel;
		};
		
		backroomPickUtils.buildOrderByModel = function(sortValue) {
			
			var orderByModel = {};
			if(!scBaseUtils.isVoid(sortValue)){
				orderByModel.OrderBy={};
				if(scBaseUtils.contains(sortValue,"Extn")){
					orderByModel.OrderBy.Extn={};
					orderByModel.OrderBy.Extn.Attribute={};
					var splitStr = sortValue.split("-");
					if(!scBaseUtils.isVoid(splitStr[0])){
						orderByModel.OrderBy.Extn.Attribute.Name = splitStr[0];
					}
					if(!scBaseUtils.isVoid(splitStr[1])){
						orderByModel.OrderBy.Extn.Attribute.Desc = splitStr[1];
					}
				}
				else{
					orderByModel.OrderBy.Attribute={};
					var splitStr = sortValue.split("-");
					if(!scBaseUtils.isVoid(splitStr[0])){
						orderByModel.OrderBy.Attribute.Name = splitStr[0];
					}
					if(!scBaseUtils.isVoid(splitStr[1])){
						orderByModel.OrderBy.Attribute.Desc = splitStr[1];
					}
				}
			}
			
			return orderByModel;
		};
		
		backroomPickUtils.buildQueryElement = function(attributeName, attributeValue, qryType) {

			var qryElement = {};
			qryElement.Name = attributeName;
			qryElement.Value = attributeValue;
			qryElement.QryType = qryType;
			return qryElement;
		};
		
		backroomPickUtils.getRemainingMinutesFromShipment = function(shipmentModel) {
			var minRemaining = "";
			if(!scBaseUtils.isVoid(shipmentModel) && !scBaseUtils.isVoid(shipmentModel.Shipment.ExpectedShipmentDate)) {
				var expShipDate = new Date(shipmentModel.Shipment.ExpectedShipmentDate);
				//Convert ExpectedShipmentDate to UTC/GMT
				var expShipDateUTCInMS = expShipDate.getTime() + expShipDate.getTimezoneOffset() * 60000;
				//Convert Current Date to UTC/GMT			
				var currentDateUTCInMS = new Date().getTime() + new Date().getTimezoneOffset() * 60000;
				
				var diffInMS = expShipDateUTCInMS - currentDateUTCInMS;
				var minRemaining = Math.floor(diffInMS / 1000 / 60);
				if(minRemaining < 0){
					minRemaining = 0;
				}
			}
			
			return minRemaining;
		};
		
		backroomPickUtils.getImageUrlForExpectedShipmentDate = function(shipmentModel, commonCodeModel) {
			var url;
			if(!scBaseUtils.isVoid(shipmentModel) && !scBaseUtils.isVoid(commonCodeModel)) {
				var remainingMin = backroomPickUtils.getRemainingMinutesFromShipment(shipmentModel);
				var codeArray = commonCodeModel.CommonCodeList.CommonCode;
				for(i=0;i<codeArray.length;i++){
					if(remainingMin <= parseInt(codeArray[i].CodeValue, 10)){
						url = codeArray[i].CodeLongDescription;
						break;
					}
				}
			}
			//Example Return Value : url = "wsc/resources/css/icons/images/timeOverdue.png"
			return url;
		};
		
		backroomPickUtils.getItemImageLocation = function(screen,widget,nameSpace,itemModel,options,dataValue){
			/*
			 * EXAMPLE VALUES
			 * url = "wsc/resources/css/icons/images/timeOverdue.png"
			 * imgLoc = "wsc/resources/css/icons/images"
			 * fullImgLoc = "wscdev/wsc/resources/css/icons/images"
			*/
			var url = scModelUtils.getStringValueFromPath("CodeLongDescription", dataValue);
			var lastSlash = url.lastIndexOf("/");
			var imgLoc = url.slice(0,lastSlash);
			var fullImgLoc = iasUIUtils.getFullURLForImage(imgLoc);
			return fullImgLoc;
				
		};
		
		backroomPickUtils.getItemImageId = function(screen,widget,nameSpace,itemModel,options,dataValue){
			/*
			 * EXAMPLE VALUES
			 * url = "wsc/resources/css/icons/images/timeOverdue.png"
			 * imgId = "timeOverdue.png"
			*/
			var url = scModelUtils.getStringValueFromPath("CodeLongDescription", dataValue);
			var lastSlash = url.lastIndexOf("/");
			var urlLength = url.length;
			var imgId = url.slice(lastSlash+1,urlLength);
			return imgId;	
		};
		
		backroomPickUtils.buildInputForOpenWizard = function(shipmentModel, landingScreen) {
			var inputModel = {};
			inputModel.Shipment={};
			inputModel.Shipment = shipmentModel.Shipment;
			inputModel.landingScreenForWizard = landingScreen;
			
			return shipmentModel;
		};
		
		backroomPickUtils.hideScreen = function(screen, skipParentScreenResize) {
			screen.schide(skipParentScreenResize);
		};
		
		backroomPickUtils.applyOverdueStyling = function(unformattedValue,screen,widgetId,namespace,shipment) {
			
			var dueInDate = "";
			if(!scBaseUtils.isVoid(shipment) && !scBaseUtils.isVoid(shipment.Shipment.TimeRemaining)) {
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
					}
				}
			}
			return dueInDate;
			
		};
		
		/*backroomPickUtils.getFormattedDueInDate = function(unformattedValue,screen,widgetId,namespace,shipment) {
		
			var dueInDate = "";
			
			if(!scBaseUtils.isVoid(shipment) && !scBaseUtils.isVoid(shipment.Shipment.ExpectedShipmentDate)) {
			
				var expShipDate = new Date(shipment.Shipment.ExpectedShipmentDate);
				//Convert ExpectedShipmentDate to UTC/GMT
				var expShipDateUTCInMS = expShipDate.getTime() + expShipDate.getTimezoneOffset() * 60000;
				//Convert Current Date to UTC/GMT			
				var currentDateUTCInMS = new Date().getTime() + new Date().getTimezoneOffset() * 60000;;
				
				if (expShipDateUTCInMS < currentDateUTCInMS) {
					scWidgetUtils.addClass(screen, widgetId, "pastDue");
					return scScreenUtils.getString(screen,"Overdue");
				}else{
					var diffInMS = expShipDateUTCInMS - currentDateUTCInMS;
				}
				
				var expShipDateInUserLocale = scBaseUtils.convertToUserFormat(new Date(shipment.Shipment.ExpectedShipmentDate),"DATETIME");
								
				var expShipmentTimeInMilliSecs = new Date(expShipDateInUserLocale).getTime();
								
				var currentTimeInMilliSecs = new Date(scBaseUtils.convertToUserFormat(currentTime,"DATETIME")).getTime();
				

				var days = Math.floor(diffInMS / 1000 / 60 / (60 * 24));
				var hours = Math.floor(diffInMS / 1000 / 60 / 60);
				var minutes = Math.floor(diffInMS / 1000 / 60);
				//var diffDate = new Date(diffInMS); Not working correctly 
				
				if(scBaseUtils.numberGreaterThan(Number(days), Number("0"))) {
					
					if(scBaseUtils.equals(Number(days),Number("1"))) {
						dueInDate = days + " "+ scScreenUtils.getString(screen,"Day") + " "+ dueInDate;
					} else {
						dueInDate = days + " "+ scScreenUtils.getString(screen,"Days") + " "+ dueInDate;
					}
					
				}
				else{
					if(scBaseUtils.numberGreaterThan(Number(hours),Number("0"))) {
				
						if(scBaseUtils.equals(Number(hours),Number("1"))) {
							dueInDate = hours + " "+ scScreenUtils.getString(screen,"Hour") + " "+ dueInDate;
						} else {
							dueInDate = hours + " "+ scScreenUtils.getString(screen,"Hours") + " "+ dueInDate;
						}
						
					}
					else{
						if(scBaseUtils.equals(Number(minutes),Number("1"))) {
							dueInDate = minutes + " "+ scScreenUtils.getString(screen,"Minute");
						} else {
							dueInDate = minutes + " "+ scScreenUtils.getString(screen,"Minutes");
						}					
					}
				}
				
			} else {
				//console.log("Expected Shipment Date is NULL. Unable to compute Due In Date");
			}
			
			//console.log("dueInDate : ",dueInDate);

			return dueInDate;
		
		};*/
		
		backroomPickUtils.lastBackroomPickInput = null;
		
		backroomPickUtils.getLastBackroomPickInput = function(){
			return backroomPickUtils.lastBackroomPickInput;
		};
		
		backroomPickUtils.setLastBackroomPickInput = function(backroomPickInput){
			backroomPickUtils.lastBackroomPickInput = backroomPickInput;
		};
		
		backroomPickUtils.isLastMashupRefId = function(mashupRefId,mashupContext){
		
			var lastMashupRefId = mashupContext.mashupArray[mashupContext.mashupArray.length - 1].mashupRefId;			
			return (lastMashupRefId==mashupRefId);
		};
		
		backroomPickUtils.processShipmentListModel = function(screen, shipmentListModel) {
		
			//console.log("processShipmentListModel : ",shipmentListModel);
						
			var shipmentModel = {};
			shipmentModel.InvalidShipment = false;
			shipmentModel.hasMultipleShipments = false;
			
			if(scBaseUtils.isVoid(shipmentListModel.Shipments.Shipment)) {
				shipmentModel.InvalidShipment = true;
				return shipmentModel;
			}
			
			if(!scBaseUtils.isVoid(shipmentListModel.Shipments.Shipment) && !scBaseUtils.isVoid(shipmentListModel.Shipments.Shipment.length) && shipmentListModel.Shipments.Shipment.length > 1) {
				shipmentModel.hasMultipleShipments = true;
				return shipmentModel;
			}
			
			shipmentModel.Shipment = shipmentListModel.Shipments.Shipment[0];
			
			//console.log("shipmentModel : ",shipmentModel);
			
			return shipmentModel;
		
		
		};
		
		backroomPickUtils.isLastShortageLineForCancellingShipment = function(parentScreen, shipmentLineModel) {
			var isLastShortageLineForCancellingShipment = false;
			var pickedShipmentModel = scScreenUtils.getTargetModel(parentScreen, "ItemScan_Output", null);
			if(!this.hasAnyLinePicked(pickedShipmentModel)){
				var hasShortagesResolved = true;		
				if(!scBaseUtils.isVoid(pickedShipmentModel) && !scBaseUtils.isVoid(pickedShipmentModel.Shipment.ShipmentLines.ShipmentLine)){
					var pickedLines = pickedShipmentModel.Shipment.ShipmentLines.ShipmentLine;
					for(i=0;i<pickedLines.length;i++){						
						if(shipmentLineModel.ShipmentLine.ShipmentLineKey!=pickedLines[i].ShipmentLineKey && scBaseUtils.isVoid(pickedLines[i].ShortageReason )){
							hasShortagesResolved = false;
							break;
						}
					}
				}
				if(hasShortagesResolved){					
					isLastShortageLineForCancellingShipment = true;
				}
			
			}
			return isLastShortageLineForCancellingShipment;
		};
		
		backroomPickUtils.isShipmentCancelled = function(mashupContext){
			var isCancelled = false;			
			for(i=0;i<mashupContext.mashupArray.length;i++){
				if("updateShortageLineToShipment"==mashupContext.mashupArray[i].mashupRefId && "9000"==mashupContext.mashupArray[i].mashupRefOutput.Shipment.Status){
					isCancelled = true;
				}
			}			
			return isCancelled;
		};
		
		backroomPickUtils.isShipmentLineMarkedShortage = function(screen,shipmentLineModel) {
			var status = false, isPickComplete = false, isLineCompletelyShorted = false;
			if(!scBaseUtils.isVoid(shipmentLineModel.ShipmentLine.ShortageQty) && (Number(shipmentLineModel.ShipmentLine.ShortageQty) > 0)) {								
				status = true;							
			}
			if(!scBaseUtils.isVoid(shipmentLineModel.ShipmentLine.BackroomPickComplete) && scBaseUtils.equals(shipmentLineModel.ShipmentLine.BackroomPickComplete, "Y")) {								
				isPickComplete = true;						
			}
			if(!scBaseUtils.isVoid(shipmentLineModel.ShipmentLine.Quantity) && scBaseUtils.numberEquals(Number(shipmentLineModel.ShipmentLine.Quantity), Number("0"))) {								
				isLineCompletelyShorted = true;						
			}
			if(status){
				
				scWidgetUtils.showWidget(screen,"shortagelbl",false,"");
				scWidgetUtils.showWidget(screen,"shortageResolutionLink",false,"");
				
				if(isPickComplete) {
					scWidgetUtils.hideWidget(screen,"shortageResolutionLink",false);
				}
				
				if(isLineCompletelyShorted) {
					scWidgetUtils.disableFields(screen,null);
					scScreenUtils.addClass(screen,"completeShort");
					scWidgetUtils.showWidget(screen,"productShortedImagePanel",false,"");
					scWidgetUtils.changeImageAlt(screen,"productShortedImagePanel",scScreenUtils.getString(screen,"Tooltip_title_completelyShort"));
					scWidgetUtils.changeImageTitle(screen,"productShortedImagePanel",scScreenUtils.getString(screen,"Tooltip_title_completelyShort"));
				}
				
			}			
			return status;
		};
		
		backroomPickUtils.isShipmentLinePreviouslyMarkedShortage = function(screen,shipmentLineModel,statusImagePanel,shortageImagePanel,shortageResolutionIcon,shortageReasonFilterSelect,txtScannedQuantity,pickedQtylbl) {
			var status = false;
			if(!scBaseUtils.isVoid(shipmentLineModel.ShipmentLine.ShortageQty) && (Number(shipmentLineModel.ShipmentLine.ShortageQty) > 0)) {								
				status = true;							
			}
			if(status){
				scWidgetUtils.hideWidget(screen,statusImagePanel,false);
				scWidgetUtils.hideWidget(screen,txtScannedQuantity,false);
				scWidgetUtils.showWidget(screen,pickedQtylbl,false,"");	
				screen.shortageResolved=true;
				if(screen.screenMode=="ItemScan"){	
					//scWidgetUtils.hideWidget(screen,shortageReasonFilterSelect,false);
					scWidgetUtils.enableReadOnlyWidget(screen,shortageReasonFilterSelect);				
				}
				else if(screen.screenMode=="ItemScanMobile"){
					scWidgetUtils.hideWidget(screen,statusImagePanel,false);									
					scWidgetUtils.hideWidget(screen,shortageResolutionIcon,false);
					scWidgetUtils.showWidget(screen,shortageImagePanel,false,"");	
				}
				
			}			
			return status;
		};
		
		backroomPickUtils.isQuantityChanged = function(qty1,qty2){
			if(Number(qty1)==Number(qty2)){
				return false;				
			}
			else{
				return true;
			}
			
			
		};
		
		backroomPickUtils.openScreen = function(screenPath, editorPath){
			/*EXAMPLE VALUES
			 *scControllerUtils.openScreenInEditor("wsc.mobile.home.subscreens.PickupInStore", {}, null, {}, {}, "wsc.editors.MobileEditor");
			 */
			scControllerUtils.openScreenInEditor(screenPath, {}, null, {}, {}, editorPath);

		};
		
		
		return backroomPickUtils;
	});