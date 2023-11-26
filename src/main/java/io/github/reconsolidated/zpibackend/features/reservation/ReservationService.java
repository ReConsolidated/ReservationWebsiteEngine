package io.github.reconsolidated.zpibackend.features.reservation;

import io.github.reconsolidated.zpibackend.authentication.appUser.AppUser;
import io.github.reconsolidated.zpibackend.features.item.Item;
import io.github.reconsolidated.zpibackend.features.item.ItemService;
import io.github.reconsolidated.zpibackend.features.item.SubItem;
import io.github.reconsolidated.zpibackend.features.store.StoreService;
import io.github.reconsolidated.zpibackend.features.storeConfig.CoreConfig;

import lombok.AllArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class ReservationService {

    private ReservationRepository reservationRepository;
    private StoreService storeService;
    private ItemService itemService;

    public Reservation reserveItem(AppUser appUser, ReservationDto request) {
        Item item = itemService.getItem(request.getItemId());
        Schedule schedule = item.getSchedule();
        CoreConfig core = item.getStore().getStoreConfig().getCore();

        ScheduleSlot requestSlot = new ScheduleSlot(request.getStartDateTime(), request.getEndDateTime(),
                request.getAmount());

        if (core.getFlexibility()) {
            //reservations with schedule
            if (!schedule.verify(core.getGranularity(), requestSlot)) {
                throw new IllegalArgumentException();
            }

            Reservation reservation = Reservation.builder()
                    .user(appUser)
                    .item(item)
                    .startDateTime(request.getStartDateTime())
                    .endDateTime(request.getEndDateTime())
                    .amount(request.getAmount())
                    .confirmed(false)
                    .build();

            schedule.processReservation(reservation);

            return reservationRepository.save(reservation);
        } else {
            if (core.getPeriodicity() || core.getSpecificReservation()) {
                //reservations with sub items
                ArrayList<SubItem> toReserve = new ArrayList<>();
                for (SubItem subItem : item.getSubItems()) {
                    for (Long subItemId: request.getSubItemIds()) {
                        if (subItem.getSubItemId().equals(subItemId)) {
                            toReserve.add(subItem);
                        }
                    }
                }
                for (SubItem subItem : toReserve) {
                    if (subItem.getAmount() <= request.getAmount()) {
                        subItem.setAmount(subItem.getAmount() - request.getAmount());
                    } else {
                        throw new IllegalArgumentException();
                    }
                }
                Reservation reservation = Reservation.builder()
                        .user(appUser)
                        .item(item)
                        .startDateTime(request.getStartDateTime())
                        .endDateTime(request.getEndDateTime())
                        .subItemIdList(request.getSubItemIds())
                        .amount(request.getAmount())
                        .confirmed(false)
                        .build();
                return reservationRepository.save(reservation);
            } else {
                //simple reservations IDK if it will be useful
                if (item.getAmount() < request.getAmount()) {
                    throw new IllegalArgumentException();
                }
                item.setAmount(item.getAmount() - requestSlot.getCurrAmount());
                Reservation reservation = Reservation.builder()
                        .user(appUser)
                        .item(item)
                        .startDateTime(request.getStartDateTime())
                        .endDateTime(request.getEndDateTime())
                        .subItemIdList(request.getSubItemIds())
                        .amount(request.getAmount())
                        .confirmed(false)
                        .build();
                return reservationRepository.save(reservation);
            }
        }
    }

    public CheckAvailabilityResponse checkAvailability(CheckAvailabilityRequest request) {

        Item item = itemService.getItem(request.getItemId());
        Schedule schedule = item.getSchedule();
        CoreConfig core = item.getStore().getStoreConfig().getCore();

        ScheduleSlot requestSlot = new ScheduleSlot(request.getStartDate(), request.getEndDate(), request.getAmount());
        if (schedule.verify(core.getGranularity(), requestSlot)) {
            return CheckAvailabilityResponseSuccess.builder()
                    .itemId(request.getItemId())
                    .amount(request.getAmount())
                    .startDate(request.getStartDate())
                    .endDate(request.getEndDate())
                    .build();
        } else {
            List<ScheduleSlot> suggestions = schedule.suggest(requestSlot);
            if (suggestions.isEmpty()) {
                return CheckAvailabilityResponseFailure.builder()
                        .itemId(item.getItemId())
                        .amount(request.getAmount())
                        .build();
            } else {
                return CheckAvailabilityResponseSuggestion.builder()
                        .itemId(item.getItemId())
                        .amount(request.getAmount())
                        .schedule(suggestions)
                        .build();
            }
        }
    }

    public List<Reservation> getUserReservations(Long currentUserId, String storeName) {
        return reservationRepository.findByUser_IdAndItemStoreStoreName(currentUserId, storeName);
    }

    public List<Reservation> getStoreReservations(AppUser currentUser, String storeName) {
        if (!currentUser.getId().equals(storeService.getStore(storeName).getOwnerAppUserId())) {
            throw new IllegalArgumentException("Only owner can get all reservations");
        }
        return reservationRepository.findByItemStoreStoreName(storeName);
    }

    public void deleteReservation(AppUser appUser, Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId).orElseThrow();
        Item item = reservation.getItem();
        Schedule schedule = item.getSchedule();
        CoreConfig core = item.getStore().getStoreConfig().getCore();

        if (core.getFlexibility()) {
            schedule.processReservationRemoval(reservation);
        } else {
            if (core.getPeriodicity() || core.getSpecificReservation()) {
                ArrayList<SubItem> toReserve = new ArrayList<>();
                for (SubItem subItem : item.getSubItems()) {
                    for (Long subItemId: reservation.getSubItemIdList()) {
                        if (subItem.getSubItemId().equals(subItemId)) {
                            toReserve.add(subItem);
                        }
                    }
                }
                for (SubItem subItem : toReserve) {
                    subItem.setAmount(subItem.getAmount() + reservation.getAmount());
                }
            } else {
                item.setAmount(item.getAmount() + reservation.getAmount());
            }
        }

        reservationRepository.delete(reservation);
    }
}
