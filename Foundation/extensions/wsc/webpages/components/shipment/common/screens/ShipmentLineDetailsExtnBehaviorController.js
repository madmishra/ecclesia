


scDefine(["scbase/loader!dojo/_base/declare","scbase/loader!dojo/_base/kernel","scbase/loader!dojo/text","scbase/loader!extn/components/shipment/common/screens/ShipmentLineDetailsExtn","scbase/loader!sc/plat/dojo/controller/ExtnServerDataController"]
 , function(			 
			    _dojodeclare
			 ,
			    _dojokernel
			 ,
			    _dojotext
			 ,
			    _extnShipmentLineDetailsExtn
			 ,
			    _scExtnServerDataController
){

return _dojodeclare("extn.components.shipment.common.screens.ShipmentLineDetailsExtnBehaviorController", 
				[_scExtnServerDataController], {

			
			 screenId : 			'extn.components.shipment.common.screens.ShipmentLineDetailsExtn'

			
			
			
			
			
						,

			
			
			 mashupRefs : 	[
	 		{
		 mashupRefId : 			'extn_getItemDetails'
,
		 mashupId : 			'extn_getItemDetailsForPick'
,
		 extnType : 			'ADD'

	}
,
	 		{
		 mashupRefId : 			'extn_updateDescription'
,
		 mashupId : 			'extn_updateDescription'
,
		 extnType : 			'ADD'

	}

	]

}
);
});

