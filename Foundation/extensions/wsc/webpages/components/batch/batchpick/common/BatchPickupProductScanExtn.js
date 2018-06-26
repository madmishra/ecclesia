
scDefine(["scbase/loader!dojo/_base/declare" , "scbase/loader!sc/plat/dojo/utils/ScreenUtils" ,"scbase/loader!extn/components/batch/batchpick/common/BatchPickupProductScanExtnUI" ,
"scbase/loader!ias/utils/UIUtils", "scbase/loader!ias/utils/WizardUtils", "scbase/loader!sc/plat/dojo/utils/WidgetUtils"
]
,
function(			 
			    _dojodeclare
			 ,_scScreenUtils,
			    _extnBatchPickupProductScanExtnUI , _iasUIUtils, _iasWizardUtils, _scWidgetUtils
){ 
	return _dojodeclare("extn.components.batch.batchpick.common.BatchPickupProductScanExtn", [_extnBatchPickupProductScanExtnUI],{
	// custom code here
 	showBatchPickingCompletionSuccess:function() {
				var textObj = {};
		            textObj["OK"] = _scScreenUtils.getString(this, "Action_Finish");
	            var msg = _scScreenUtils.getString(this, "Message_AllBatchLinesPicked");
				var argsObj = {type:"success",text:_scScreenUtils.getString(this,"textSuccess"),info:msg,iconClass:"messageSuccessIcon"};
				_scScreenUtils.showSuccessMessageBox(
					this, msg , "handleFinshBatchPickup", textObj);
        },
	removeCloseButton: function (event, bEvent, ctrl, args) {
				var wizardInstance = _iasUIUtils.getWizardForScreen(this)
				_scWidgetUtils.hideWidget(wizardInstance, "nextBttn", true)
				_scWidgetUtils.hideWidget(wizardInstance, "nextBttn2", true)
	}
});
});

