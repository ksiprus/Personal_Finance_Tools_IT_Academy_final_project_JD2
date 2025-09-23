package by.ksiprus.Personal_Finance_Tools.user_service.models;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PageOfUser {
    int number;
    int size;
    int total_pages;
    long total_elements;
    boolean first;
    int number_of_elements;
    boolean last;
    List<User> content;
}
