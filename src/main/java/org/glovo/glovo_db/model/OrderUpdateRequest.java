package org.glovo.glovo_db.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderUpdateRequest {
    private Date modificationDateTime;
    private double totalAmount;
    private String mobile;

}
