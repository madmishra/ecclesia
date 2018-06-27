scDefine(
	[
		"scbase/loader!dojo/_base/declare",
		"scbase/loader!extn/components/shipment/backroompick/BackroomPickProductScanExtnUI",
		"scbase/loader!sc/plat/dojo/utils/ScreenUtils",
		"scbase/loader!sc/plat/dojo/utils/BaseUtils",
		"scbase/loader!ias/utils/UIUtils",
		"scbase/loader!sc/plat/dojo/utils/WizardUtils",
		"scbase/loader!sc/plat/dojo/utils/EventUtils",
		"scbase/loader!ias/utils/WizardUtils",
	],
	function (_dojodeclare, _extnBackroomPickProductScanExtnUI, _scScreenUtils, _scBaseUtils, _iasUIUtils, _scWizardUtils, _scEventUtils, _iasWizardUtils) {
		return _dojodeclare(
			"extn.components.shipment.backroompick.BackroomPickProductScanExtn", [_extnBackroomPickProductScanExtnUI], {
				// custom code here
				startScannerInApp: function (event, bEvent, ctrl, args) {
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
					}
					if (window.webkit) {
						window.webkit.messageHandlers.startScanner.postMessage({});
					}
				},
				stopScannerInput: function (event, bEvent, ctrl, args) {
					console.log("stopping mobile scanner");
					var self = this;
					if (window.webkit) {
						window.webkit.messageHandlers.stopScanner.postMessage({});
					}
				},
				gotoNextScreen: function () {
					console.log('going to the next screen');
					this.stopScannerInput();
					_scScreenUtils.clearScreen(
						this, "translateBarCode_input");
					var parentScreen = null;
					parentScreen = _iasUIUtils.getParentScreen(
						this, true);
					if (
						_scWizardUtils.isCurrentPageLastEntity(
							parentScreen)) {
						_iasWizardUtils.setActionPerformedOnWizard(
							parentScreen, "CONFIRM");
					} else {
						_iasWizardUtils.setActionPerformedOnWizard(
							parentScreen, "NEXT");
					}
					_scEventUtils.fireEventToParent(
						this, "onSaveSuccess", null);
				}
			}
		);
	}
);