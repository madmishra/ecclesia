scDefine(["scbase/loader!dojo/_base/declare", "scbase/loader!dojo/dom-attr", "scbase/loader!dijit/registry", "scbase/loader!dojo/dom-style", "scbase/loader!sc/plat/dojo/utils/ScreenUtils", "scbase/loader!sc/plat/dojo/utils/BaseUtils", "scbase/loader!extn/mobile/common/screens/shipment/picking/ShipmentPickDetailsExtnUI", "scbase/loader!sc/plat/dojo/utils/WidgetUtils", "scbase/loader!ias/utils/UIUtils"],
	function (
		_dojodeclare, domattr, registry, domStyle,
		_scScreenUtils, _scBaseUtils,
		_extnShipmentPickDetailsExtnUI, _scWidgetUtils, _iasUIUtils
	) {
		return _dojodeclare("extn.mobile.common.screens.shipment.picking.ShipmentPickDetailsExtn", [_extnShipmentPickDetailsExtnUI], {
			// custom code here
			continueGiftWrap: function (event, bEvent, ctrl, args) {
				var targetModel = null;
				shipmentDetails = _scBaseUtils.getTargetModel(
					this, "common_getShipmentDetails_input", null);
				var editorName = "wsc.mobile.editors.MobileEditor";
				_iasUIUtils.openWizardInEditor("wsc.components.shipment.summary.ShipmentSummaryWizard", shipmentDetails, editorName, this);
			},

			hideandshowbutton: function () {
				var continueLabel0 = "lnk_ShipAction";
				var continueLabel = "lnk_PickAction";
				var continueLabel1 = "lnk_AssignToHoldAction";
				var continueLabel2 = "lnk_RecordCustomerPickupAction";
				var giftWrap = "extn_screenbase_giftwrap";
				var giftshow = null;
				var giftwrapRef = this.getWidgetByUId("extn_screenbase_giftwrap");
				var self = this;
				giftwrapModel = _scScreenUtils.getModel(this, "Shipment");
				_scWidgetUtils.hideWidget(self, giftWrap, true);
				if (giftwrapModel.Shipment && giftwrapModel.Shipment.Status) {
					var status = giftwrapModel.Shipment.Status.Status;
					//	console.log('inside first if'); 
					if (status === "1100.70.200") {
						//		console.log('inside seoncd if'); 
						_scWidgetUtils.hideWidget(self, continueLabel0, true);
						_scWidgetUtils.hideWidget(self, continueLabel, true);
						_scWidgetUtils.hideWidget(self, continueLabel1, true);
						_scWidgetUtils.hideWidget(self, continueLabel2, true);
						_scWidgetUtils.showWidget(self, giftWrap, true, null);

					}
				}
			},
			showHideHoldLocation: function (
				dataValue, screen, widget, nameSpace, shipmentModel) {
				// _wscShipmentUtils.showHideHoldLocation(
				// 	this, shipmentModel, widget);
				if(dataValue)
					return dataValue;
				else
					return "Bin location not assigned";
			},
			trythisLater: function () {
				var identifierMode = null;
				var customerRef = null;
				var customerRef0 = null;
				var customerRef1 = null;
				var customerRef2 = null;
				var customer0 = null;
				var customer1 = null;
				var customer2 = null;
				var customer = null;

				identifierMode = _scBaseUtils.getAttributeValue("identifierId", false, this);
				console.log('identifier', identifierMode);
				switch (identifierMode) {
					case "Pick":
						customerRef0 = this.getWidgetByUId("lnk_PickAction");
						if (customerRef0) customer0 = dijit.byId(customerRef0.id);
						customerRef1 = this.getWidgetByUId("lnk_AssignToHoldAction");
						if (customerRef1) customer1 = dijit.byId(customerRef1.id);
						customerRef2 = this.getWidgetByUId("lnk_RecordCustomerPickupAction");
						if (customerRef2) customer2 = dijit.byId(customerRef2.id);


						break;
					case "Ship":
						customerRef = this.getWidgetByUId("lnk_ShipAction");
						customer = dijit.byId(customerRef.id);
						break;
					case "Pack":
						customerRef = this.getWidgetByUId("lnk_PackAction");
						break;

				}







				var giftwrap = null;
				var giftwrapRef = this.getWidgetByUId("extn_screenbase_giftwrap");
				console.log('giftwrapRef', giftwrapRef);
				if (giftwrapRef) {
					giftwrap = dijit.byId(giftwrapRef.id);
					domStyle.set(giftwrap.domNode, 'display', 'none');
				}
				//	console.log('giftwrapRef', giftwrapRef);
				giftwrapModel = _scScreenUtils.getModel(this, "Shipment");
				//	console.log('giftwrapModel', giftwrapModel);
				if (giftwrapModel.Shipment && giftwrapModel.Shipment.Status) {
					var status = giftwrapModel.Shipment.Status.Status;
					if (status === "1100.70.200" && identifierMode === "Ship") {

						if (giftwrapRef) {
							domStyle.set(giftwrap.domNode, 'display', 'table-cell');

							domStyle.set(customer.domNode, 'display', 'none');
						} else {
							domStyle.set(customer.domNode, 'display', 'table-cell');
							domStyle.set(giftwrap.domNode, 'display', 'none');
						}
					}
					if (status === "1100.70.200" && identifierMode == "Pick") {

						if (giftwrapRef) {
							debugger
							domStyle.set(giftwrap.domNode, 'display', 'table-cell');
							domStyle.set(customer0.domNode, 'display', 'none');
							domStyle.set(customer1.domNode, 'display', 'none');
							domStyle.set(customer2.domNode, 'display', 'none');
						} else {
							debugger
							domStyle.set(customer0.domNode, 'display', 'table-cell');
							domStyle.set(customer1.domNode, 'display', 'table-cell');
							domStyle.set(customer2.domNode, 'display', 'table-cell');
							//domStyle.set(giftwrap.domNode, 'display', 'none');
						}
					}
				}
			}
		});
	});