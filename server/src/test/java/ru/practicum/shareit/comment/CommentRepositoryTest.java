package ru.practicum.shareit.comment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.repository.UserRepository;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
class CommentRepositoryTest {
    @Autowired
    private TestEntityManager em;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private RequestRepository requestRepository;

//    @BeforeEach
//    public void preTestInitialization() {
//
//    }
//
//    @Test
//    public void test_T0010_PS01() {
//
//    }
//
//    @Nested
//    class RepositoryWithWithFilledDataBase {
//        @BeforeEach
//        public void fillRepositoryWithAssigningId() {
//
//        }
//
//        @Test
//        public void test_T1010_PS01() {
//
//        }
//    }
}