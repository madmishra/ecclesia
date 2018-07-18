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
    });
});