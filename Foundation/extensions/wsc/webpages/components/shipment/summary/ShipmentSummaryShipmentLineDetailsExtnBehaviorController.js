


scDefine(["scbase/loader!dojo/_base/declare","scbase/loader!dojo/_base/kernel","scbase/loader!dojo/text","scbase/loader!extn/components/shipment/summary/ShipmentSummaryShipmentLineDetailsExtn","scbase/loader!sc/plat/dojo/controller/ExtnServerDataController"]
 , function(			 
			    _dojodeclare
			 ,
			    _dojokernel
			 ,
			    _dojotext
			 ,
			    _extnShipmentSummaryShipmentLineDetailsExtn
			 ,
			    _scExtnServerDataController
){

return _dojodeclare("extn.components.shipment.summary.ShipmentSummaryShipmentLineDetailsExtnBehaviorController", 
				[_scExtnServerDataController], {

			
			 screenId : 			'extn.components.shipment.summary.ShipmentSummaryShipmentLineDetailsExtn'

			
			
			
			
			
						,

			
			
			 mashupRefs : 	[
	 		{
		 mashupRefId : 			'extn_itemDetails'
,
		 mashupId : 			'extn_itemDetailsForPRoduct'
,
		 extnType : 			'ADD'

	}

	]

}
);
});

