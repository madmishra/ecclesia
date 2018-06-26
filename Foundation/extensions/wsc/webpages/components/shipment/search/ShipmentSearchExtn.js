
scDefine(["scbase/loader!dojo/_base/declare", "scbase/loader!sc/plat/dojo/utils/ScreenUtils", "scbase/loader!extn/components/shipment/search/ShipmentSearchExtnUI"]
	,
	function (
		_dojodeclare, _scScreenUtils
		,
		_extnShipmentSearchExtnUI
	) {
		return _dojodeclare("extn.components.shipment.search.ShipmentSearchExtn", [_extnShipmentSearchExtnUI], {
			// custom code here
			
			populateExtnStatuses: function () {
				statusModel = _scScreenUtils.getModel(this, "getShipmentStatusList_output");
				if (statusModel.StatusList && statusModel.StatusList.Status) {
					statusModel.StatusList.Status.push({ StatusCode: "1100.70.06.30.1", StatusKey: "Option_ReadyForPrint" })
					statusModel.StatusList.Status.push({ StatusCode: "1100.70.200", StatusKey: "Option_ReadyForGiftWrap" })
				}

			//	console.log('statusModel', statusModel);
				_scScreenUtils.setModel(this, "getShipmentStatusList_output", statusModel, null);
			}
		});
	});

