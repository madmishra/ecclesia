scDefine(["scbase/loader!dojo/_base/declare", "scbase/loader!ias/utils/ContextUtils", "scbase/loader!ias/utils/RepeatingScreenUtils", "scbase/loader!ias/utils/ScreenUtils", "scbase/loader!sc/plat/dojo/utils/BaseUtils", "scbase/loader!sc/plat/dojo/utils/ModelUtils", "scbase/loader!sc/plat/dojo/utils/ScreenUtils", "scbase/loader!extn/mobile/custom/sort/SortScanUI", "scbase/loader!wsc/mobile/home/utils/MobileHomeUtils","scbase/loader!ias/utils/EventUtils","scbase/loader!sc/plat/dojo/utils/WidgetUtils","scbase/loader!ias/utils/UIUtils"], 
function(
_dojodeclare, _iasContextUtils, _iasRepeatingScreenUtils, _iasScreenUtils, _scBaseUtils, _scModelUtils, _scScreenUtils, _extnSortScanUI, _wscMobileHomeUtils,_iasEventUtils,_scWidgetUtils,_iasUIUtils) {
    return _dojodeclare("extn.mobile.custom.sort.SortScan", [_extnSortScanUI], {
        // custom code here
        scanProductOnEnter: function (
            event, bEvent, ctrl, args) {
            if (
                _iasEventUtils.isEnterPressed(
                    event)) {
                this.scanProduct();
            }
        },
        scanProduct: function () {
            var barCodeModel = null;
            var barCodeData = null;
                barCodeModel = _scScreenUtils.getTargetModel(
                    this, "translateBarCode_input", null);
				console.log('barCodeModel',barCodeModel); 	
                barCodeData = _scModelUtils.getStringValueFromPath("BarCode.BarCodeData", barCodeModel);
                if (
                    _scBaseUtils.isVoid(
                        barCodeData)) {
                    _iasScreenUtils.showErrorMessageBoxWithOk(
                        this, "NoProductScanned");
                } else {
                 //   _iasUIUtils.callApi(
                //        this, barCodeModel, "translateBarCode", null);
                //    var eventDefn = null;
                //    var eventArgs = null;
                //    eventArgs = {};
                //    eventDefn = {};
                //    _scBaseUtils.setAttributeValue("argumentList", _scBaseUtils.getNewBeanInstance(), eventDefn);
                 //   this.clearItemFilter();
				 _iasUIUtils.callApi(this, {ShipmentLine : {ItemID : barCodeData}}, "extn_getDetails", null);

                }
               
            
        },
        setFocusOnScanField: function (
            event, bEvent, ctrl, args) {
            if (!(
                _iasContextUtils.isMobileContainer())) {
                _scWidgetUtils.setFocusOnWidgetUsingUid(
                    this, "scanProductIdTxt");
            }
        },
        clearItemFilter: function(
            event, bEvent, ctrl, args) {
                var tempModel = null;
                tempModel = {};
                _scScreenUtils.setModel(
                this, "translateBarCode_output", tempModel, null);
                _scWidgetUtils.setFocusOnWidgetUsingUid(
                this, "scanProductIdTxt");
            },
        handleSort: function(modelOutput){
            var shipmentKey = ""; 
                var size = 0;
                var i = 0;
                var counter = 0;
                debugger
                if (modelOutput.ShipmentLines.ShipmentLine) {
                    size = modelOutput.ShipmentLines.ShipmentLine.length;
                }
                while (i < size) {

                        
                    if (modelOutput.ShipmentLines.ShipmentLine[i].Shipment[0].PackListType === "Sort") {

                           if(modelOutput.ShipmentLines.ShipmentLine[i].Shipment[0].AssignedToUserId === _scUserprefs.getUserId())
                           {
                               counter = i;
                               break;
                           }

                            i++;
                        if (i === size) { break; }

                    }
                    else {
                        counter = i;
                        break;
                    }
                }
                if (counter === 0) {
                   
                    _wscMobileHomeUtils.openScreen("wsc.mobile.home.MobileHome", "wsc.mobile.editors.MobileEditor");
                    _iasScreenUtils.showErrorMessageBoxWithOk(
                        this, "No shipments found");
                    
                }
                if (modelOutput.ShipmentLines && modelOutput.ShipmentLines.ShipmentLine[counter].ShipmentKey) {
                    //  var size = modelOutput.ShipmentLines.ShipmentLine.Shipment.length;

                    // for (var i = 0; i <= size; i++) {

                    shipmentKey = modelOutput.ShipmentLines.ShipmentLine[counter].ShipmentKey;
                }
                //shipmentKey = modelOutput.ShipmentLines.ShipmentLine[0].ShipmentKey
                // shipmentKey = this.checkForSort(modelOutput);
                // console.log('shipmentKey', shipmentKey);
                if (shipmentKey && counter !== 0)
                    _iasUIUtils.openWizardInEditor("extn.wizards.SortScanPickWizard", { Shipment: { ShipmentKey: shipmentKey } }, "wsc.mobile.editors.MobileEditor", this, null);
                //  }
            
        }    
    });
});