    package com.warehouse.dao;


    import  java.util.ArrayList;
    import java.util.List;
    import com.warehouse.models.Customer;
            import java.sql.*;

    public class CustomerDAO 
    {
        // function for fcferaretion custoemr
        public boolean addCustomer ( Customer cus) {
            String sql= "INSERT  INTO customers (name, contact_info, address)  VALUES (?, ?, ?)" ;
            // Update when we need more complex customer data - phonenumber, ...

            try (
                    Connection c = DatabaseConfig.getConnection();
                    PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)
                    // safe injection for statement before exe
                    // retturn gen key -> custoemr_id from database

                    )
            {
                ps.setString(1, cus.getName());
                ps.setString(2, cus.getContactInfo());
                ps.setString(3, cus.getAddress());

                int returnRows = ps.executeUpdate();
                if( returnRows > 0) {
                // Success if more than 1 row returned
                    try(ResultSet gk = ps.getGeneratedKeys()){
                        if( gk.next()) { // Why next() ???
                            cus.setPersonId(gk.getInt(1));
                        }
                    }
                    System.out.print("added Customer" + cus.getName());
                    return true;
                }
            }
            catch (SQLException e){
                System.err.println(e.getMessage());
            }
            return false; // False mean Error iin adding
        } 


        // function to Read all costuemrs
        public List<Customer> getAllCustomers() {
            List<Customer> cuss = new ArrayList<>();
            String sql = " SELECT * FROM CUSTOMERS";
            try(
                    Connection c = DatabaseConfig.getConnection();
                    Statement s = c.createStatement();
                    ResultSet rs = s.executeQuery(sql);
                ) {
            while(rs.next()) { // Loop untill we have all cus in cuss
                Customer cus = new  Customer(
                    rs.getInt("customer_id"),
                    rs.getString("name"),
                    rs.getString("contact_info"),
                    rs.getString("address"));
                cuss.add(cus); // add to list of cuss
                }
            } catch( SQLException e){ System.err.println(e.getMessage());}

            return cuss;
        }

    }

