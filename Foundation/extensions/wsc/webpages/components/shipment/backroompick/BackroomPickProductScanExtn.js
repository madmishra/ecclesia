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
						self.scanProduct();
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
				printDetails: function (secondaryDetails) {
					var batchModel = _scScreenUtils.getModel(this, "backroomPickShipmentDetails_output");
					var expectedShipmentDate = Date(batchModel.Shipment.ExpectedShipmentDate);
					var printModel = {};
					printModel.PickupDate = Date(expectedShipmentDate);
					printModel.OrderNo = batchModel.Shipment.DisplayOrderNo;
					if (secondaryDetails.Order) {
						if (secondaryDetails.Order.PersonInfoBillTo) {
							printModel.PrimaryFirstName = secondaryDetails.Order.PersonInfoBillTo.FirstName;
							printModel.PrimarySecondName = secondaryDetails.Order.PersonInfoBillTo.LastName;
						}
					}
					if (secondaryDetails.Order.OrderLines && secondaryDetails.Order.OrderLines.OrderLine) {
						var tempArray = secondaryDetails.Order.OrderLines.OrderLine;
						if (tempArray && tempArray[0].AdditionalAddresses && tempArray[0].AdditionalAddresses.AdditionalAddress[0] && tempArray[0].AdditionalAddresses.AdditionalAddress[0].PersonInfo) {
							var firstName = tempArray[0].AdditionalAddresses.AdditionalAddress[0].PersonInfo.FirstName;
							var secondName = tempArray[0].AdditionalAddresses.AdditionalAddress[0].PersonInfo.LastName;
							printModel.SecondaryFirstName = firstName;
							printModel.SecondaryLastName = secondName;
						}
					}
					console.log('Invoking print with the following data - ', printModel);
					if (window.webkit) {
						window.webkit.messageHandlers.invokePrint.postMessage(printModel);
					}
				},
				invokePrint: function () {
					console.log("Invoking print");
					var batchModel = _scScreenUtils.getModel(this, "backroomPickShipmentDetails_output");
					var orderNo = batchModel.Shipment.DisplayOrderNo;
					_iasUIUtils.callApi(this, {
						Order: {
							OrderNo: orderNo
						}
					}, "extn_getPrintDetails", null);
				},
				gotoNextScreen: function () {
					console.log('going to the next screen');
					this.invokePrint();
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