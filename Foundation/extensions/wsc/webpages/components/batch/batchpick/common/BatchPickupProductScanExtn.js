scDefine(["scbase/loader!dojo/_base/declare", "scbase/loader!sc/plat/dojo/utils/ScreenUtils", "scbase/loader!extn/components/batch/batchpick/common/BatchPickupProductScanExtnUI",
		"scbase/loader!ias/utils/UIUtils", "scbase/loader!ias/utils/WizardUtils", "scbase/loader!sc/plat/dojo/utils/WidgetUtils", "scbase/loader!sc/plat/dojo/utils/ModelUtils", "scbase/loader!sc/plat/dojo/utils/BaseUtils", "scbase/loader!sc/plat/dojo/utils/EditorUtils"
	],
	function (
		_dojodeclare, _scScreenUtils,
		_extnBatchPickupProductScanExtnUI, _iasUIUtils, _iasWizardUtils, _scWidgetUtils, _scModelUtils, _scBaseUtils, _scEditorUtils
	) {
		return _dojodeclare("extn.components.batch.batchpick.common.BatchPickupProductScanExtn", [_extnBatchPickupProductScanExtnUI], {
			// custom code here
			showBatchPickingCompletionSuccess: function () {
				var textObj = {};
				textObj["OK"] = _scScreenUtils.getString(this, "Action_Finish");
				var msg = _scScreenUtils.getString(this, "Message_AllBatchLinesPicked");
				var argsObj = {
					type: "success",
					text: _scScreenUtils.getString(this, "textSuccess"),
					info: msg,
					iconClass: "messageSuccessIcon"
				};
				_scScreenUtils.showSuccessMessageBox(
					this, msg, "handleFinshBatchPickup", textObj);
			},
			removeCloseButton: function (event, bEvent, ctrl, args) {
				var wizardInstance = _iasUIUtils.getWizardForScreen(this)
				_scWidgetUtils.hideWidget(wizardInstance, "nextBttn", true)
				_scWidgetUtils.hideWidget(wizardInstance, "nextBttn2", true)
			},
			updateShortageForBatchLine: function (event, bEvent, ctrl, args) {

				var batchLineModel = _scBaseUtils.getValueFromPath("BatchLineShortedModel", args);
				console.log('args', args);
				_scModelUtils.setStringValueAtModelPath("StoreBatch.StoreBatchKey", _scModelUtils.getStringValueFromPath("StoreBatch.StoreBatchKey", _scScreenUtils.getInitialInputData(_scEditorUtils.getCurrentEditor())), batchLineModel);
				batchLineModel.StoreBatch.ShortageReasonCodeActual = args.BatchLineShortedModel.StoreBatch.Item.ShortageReasonActual;
				console.log('batchLineModel', batchLineModel);
				_iasUIUtils.callApi(this, batchLineModel, "recordShortageForBatchPick", null);

			},
			startScannerInApp: function () {
				console.log("starting mobile scanner");
				var self = this;
				window.populateInputField = function (scannedValue) {
					console.log("Recieved the response -", scannedValue);
					var scanInput = _scScreenUtils.getWidgetByUId(
						self,
						"scanProductIdTxt"
					);
					var scanInputRef = dijit.byId(scanInput.id);
					scanInputRef.set("value", scannedValue);
					self.scanProduct();
				}
				if (window.webkit) {
					window.webkit.messageHandlers.startScanner.postMessage({});
				}
			},
			startScan: function () {
				this.startScannerInApp();
			}
		});
	});