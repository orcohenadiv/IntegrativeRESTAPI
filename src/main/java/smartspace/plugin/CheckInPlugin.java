package smartspace.plugin;

import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;

import smartspace.dao.EnhancedElementDao;
import smartspace.data.ActionEntity;
import smartspace.data.ElementEntity;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;

@Component
public class CheckInPlugin implements Plugin {

	private ObjectMapper jackson;
    private EnhancedElementDao<String> elementDao;
	
	@Autowired
	public CheckInPlugin() {
		super();
		this.jackson = new ObjectMapper();
	}
	
	@Override
	public ActionEntity process(ActionEntity actionEntity) {
		
		try {
			CheckInInput checkInInput = 
					this.jackson.readValue(
							this.jackson.writeValueAsString(actionEntity.getMoreAttributes()), 
							CheckInInput.class);
				
			if(checkInInput.getCheckIn() != null) {
				actionEntity.getMoreAttributes().put("Checked In:", "SUCCESSFULLY");
			}
			else {
				actionEntity.getMoreAttributes().put("Checked In At:", new Date());
			}
						
			return actionEntity;
			
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}










