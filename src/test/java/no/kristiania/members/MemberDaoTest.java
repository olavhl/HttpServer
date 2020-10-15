package no.kristiania.members;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Member;


public class MemberDaoTest {

    private MemberDao memberDao;

    @Test
    void shouldRetrieveSavedMembers() {
        Member member = sampleMember();
        int id = memberDao.insert(member);
        assertThat(memberDao.retrieve(id).hasNoNullFieldsOrProperties().isEqualToComparingFieldByField(member));
    }
}
