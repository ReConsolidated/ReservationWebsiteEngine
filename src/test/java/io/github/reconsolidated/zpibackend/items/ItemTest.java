package io.github.reconsolidated.zpibackend.items;

import io.github.reconsolidated.zpibackend.domain.appUser.AppUser;
import io.github.reconsolidated.zpibackend.domain.appUser.AppUserService;
import io.github.reconsolidated.zpibackend.domain.comment.CommentDto;
import io.github.reconsolidated.zpibackend.domain.comment.CommentService;
import io.github.reconsolidated.zpibackend.domain.item.Item;
import io.github.reconsolidated.zpibackend.domain.item.ItemMapper;
import io.github.reconsolidated.zpibackend.domain.item.ItemService;
import io.github.reconsolidated.zpibackend.domain.item.dtos.ItemAttributesDto;
import io.github.reconsolidated.zpibackend.domain.item.dtos.ItemDto;
import io.github.reconsolidated.zpibackend.domain.item.dtos.ScheduleDto;
import io.github.reconsolidated.zpibackend.domain.store.Store;
import io.github.reconsolidated.zpibackend.domain.store.StoreService;
import io.github.reconsolidated.zpibackend.domain.store.dtos.CreateStoreDto;
import io.github.reconsolidated.zpibackend.domain.storeConfig.*;
import io.github.reconsolidated.zpibackend.domain.storeConfig.dtos.OwnerDto;
import io.github.reconsolidated.zpibackend.domain.storeConfig.dtos.StoreConfigDto;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class ItemTest {
    @Autowired
    private ItemService itemService;
    @Autowired
    private ItemMapper itemMapper;
    @Autowired
    private AppUserService appUserService;
    @Autowired
    private StoreConfigService storeConfigService;
    @Autowired
    private StoreService storeService;
    @Autowired
    private CommentService commentService;


    @Test
    @Transactional
    public void calculateAverageRating() {
        AppUser appUser = appUserService.getOrCreateUser("unique_id", "abc@def.com", "name", "lastname");
        StoreConfig storeConfig = storeConfigService.createStoreConfig(appUser, getStoreConfigDto());
        Store store = storeService.createStore(appUser,
                new CreateStoreDto(
                storeConfig.getStoreConfigId(),
                storeConfig.getName())
        );
        ItemDto dto = getItemDto();
        Item item = itemService.createItem(appUser, storeConfig.getName(), dto);
        CommentDto commentDto = new CommentDto(
                null,
                appUser.getId(),
                item.getItemId(),
                "user_nickname",
                "comment contents",
                LocalDateTime.now(),
                3.0
        );
        commentService.addComment(appUser, item.getItemId(), commentDto);
        commentDto = new CommentDto(
                null,
                appUser.getId(),
                item.getItemId(),
                "user_nickname",
                "comment contents",
                LocalDateTime.now(),
                5.0
        );
        commentService.addComment(appUser, item.getItemId(), commentDto);

        ItemDto resultItem = itemService.getItemDto(item.getItemId());
        assertThat(resultItem.getMark()).isEqualTo(4.0);
    }

    private StoreConfigDto getStoreConfigDto() {
        CoreConfig coreConfig = new CoreConfig();
        OwnerDto owner = new OwnerDto();
        owner.setStoreName("store_name");
        MainPageConfig mainPageConfig = new MainPageConfig();
        DetailsPageConfig detailsPageConfig = new DetailsPageConfig();


        return new StoreConfigDto(
                null,
                owner,
                coreConfig,
                mainPageConfig,
                detailsPageConfig,
                new ArrayList<>(),
                AuthenticationConfig.builder().build()
        );
    }

    @NotNull
    private static ItemDto getItemDto() {
        ItemAttributesDto attributesDto = new ItemAttributesDto();
        attributesDto.setTitle("");
        attributesDto.setDescription("");
        attributesDto.setImage("");
        attributesDto.setSubtitle("");

        ScheduleDto scheduleDto = new ScheduleDto();
        scheduleDto.setStartDateTime(LocalDateTime.now());
        scheduleDto.setEndDateTime(LocalDateTime.now().plusHours(5));
        return new ItemDto(
                null,
                true,
                attributesDto,
                new ArrayList<>(),
                new ArrayList<>(),
                scheduleDto,
                new ArrayList<>(),
                1,
                1,
                null,
                null,
                null
        );
    }

}