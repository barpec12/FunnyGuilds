package net.dzikoysk.funnyguilds.data.configs;

import com.google.common.collect.ImmutableMap;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.basic.guild.GuildRegex;
import net.dzikoysk.funnyguilds.basic.rank.RankSystem;
import net.dzikoysk.funnyguilds.basic.rank.RankUtils;
import net.dzikoysk.funnyguilds.element.notification.NotificationStyle;
import net.dzikoysk.funnyguilds.element.notification.bossbar.provider.BossBarOptions;
import net.dzikoysk.funnyguilds.util.Cooldown;
import net.dzikoysk.funnyguilds.util.IntegerRange;
import net.dzikoysk.funnyguilds.util.commons.ChatUtils;
import net.dzikoysk.funnyguilds.util.commons.TimeUtils;
import net.dzikoysk.funnyguilds.util.commons.bukkit.ItemBuilder;
import net.dzikoysk.funnyguilds.util.commons.bukkit.ItemUtils;
import net.dzikoysk.funnyguilds.util.commons.bukkit.MaterialUtils;
import net.dzikoysk.funnyguilds.util.nms.Reflections;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.diorite.cfg.annotations.CfgClass;
import org.diorite.cfg.annotations.CfgCollectionStyle;
import org.diorite.cfg.annotations.CfgCollectionStyle.CollectionStyle;
import org.diorite.cfg.annotations.CfgComment;
import org.diorite.cfg.annotations.CfgExclude;
import org.diorite.cfg.annotations.CfgName;
import org.diorite.cfg.annotations.CfgStringStyle;
import org.diorite.cfg.annotations.CfgStringStyle.StringStyle;
import org.diorite.cfg.annotations.defaults.CfgDelegateDefault;

import java.io.File;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@CfgClass(name = "PluginConfiguration")
@CfgDelegateDefault("{new}")
@CfgComment("~-~-~-~-~-~-~-~-~-~-~-~~-~-~-~~ #")
@CfgComment("                                #")
@CfgComment("          FunnyGuilds           #")
@CfgComment("         4.7.0 Tribute          #")
@CfgComment("                                #")
@CfgComment("~-~-~-~-~-~-~-~-~-~-~-~~-~-~-~~ #")
@CfgComment("FunnyGuilds wspiera PlaceholderAPI, lista dodawanych placeholderow znajduje sie tutaj:")
@CfgComment("https://www.spigotmc.org/wiki/placeholderapi-plugin-placeholders-page-2/#funnyguilds")
@CfgComment(" ")
@CfgComment("FunnyGuilds wspiera takze placeholdery w BungeeTabListPlus i MVdWPlaceholderAPI")
@CfgComment("Placeholdery sa dokladnie takie same jak w przypadku PlaceholderAPI (bez znaku % oczywiscie)")
@CfgComment(" ")
@CfgComment("Jezeli chcesz, aby dana wiadomosc byla pusta, zamiast wiadomosci umiesc: ''")
@CfgComment(" ")
public class PluginConfiguration {

    @CfgExclude
    public final Cooldown<Player> informationMessageCooldowns = new Cooldown<>();

    @CfgExclude
    public SimpleDateFormat dateFormat;

    @CfgComment("Wyswietlana nazwa pluginu")
    @CfgName("plugin-name")
    @CfgStringStyle(StringStyle.ALWAYS_QUOTED)
    public String pluginName = "FunnyGuilds";

    @CfgComment("Czy plugin ma dzialac w trybie debug (wysylanie dodatkowych wiadomosci w celu zdiagnozowania bledow itp.)")
    @CfgName("debug-mode")
    public boolean debugMode = false;

    @CfgComment("Czy informacje o aktualizacji maja byc widoczne podczas wejscia na serwer")
    @CfgName("update-info")
    public boolean updateInfo = true;

    @CfgComment("Czy informacje o aktualizacji wersji nightly maja byc widoczne podczas wejscia na serwer")
    @CfgComment("Ta opcja działa tylko wtedy, gdy także jest włączona opcja 'update-info'")
    @CfgName("update-nightly-info")
    public boolean updateNightlyInfo = true;

    @CfgComment("Mozliwosc zakladania gildii (mozna zmienic takze za pomoca komendy /ga enabled)")
    @CfgName("guilds-enabled")
    public boolean guildsEnabled = true;

    @CfgComment("Czy tworzenie regionow gildii (i inne zwiazane z nimi rzeczy) maja byc wlaczone")
    @CfgComment("UWAGA - dobrze przemysl decyzje o wylaczeniu regionow!")
    @CfgComment(
            "Gildie nie beda mialy w sobie zadnych informacji o regionach, a jesli regiony sa wlaczone - te informacje musza byc obecne")
    @CfgComment("Jesli regiony mialyby byc znowu wlaczone - bedzie trzeba wykasowac WSZYSTKIE dane pluginu")
    @CfgComment("Wylaczenie tej opcji nie powinno spowodowac zadnych bledow, jesli juz sa utworzone regiony gildii")
    @CfgName("regions-enabled")
    public boolean regionsEnabled = true;

    @CfgComment("Czy gracz po smierci ma sie pojawiac w bazie swojej gildii")
    @CfgComment("Dziala tylko jesli regiony sa wlaczone")
    @CfgName("respawn-in-base")
    public boolean respawnInBase = true;

    @CfgComment("Maksymalna dlugosc nazwy gildii")
    @CfgName("name-length")
    public int createNameLength = 22;

    @CfgComment("Minimalna dlugosc nazwy gildii")
    @CfgName("name-min-length")
    public int createNameMinLength = 4;

    @CfgComment("Maksymalna dlugosc tagu gildii")
    @CfgName("tag-length")
    public int createTagLength = 4;

    @CfgComment("Minimalna dlugosc tagu gildii")
    @CfgName("tag-min-length")
    public int createTagMinLength = 2;

    @CfgComment("Zasada sprawdzania nazwy gildii przy jej tworzeniu")
    @CfgComment("Dostepne zasady:")
    @CfgComment("LOWERCASE - umozliwia uzycie tylko malych liter")
    @CfgComment("UPPERCASE - umozliwia uzycie tylko duzych liter")
    @CfgComment("DIGITS - umozliwia uzycie tylko cyfr")
    @CfgComment("LOWERCASE_DIGITS - umozliwia uzycie malych liter i cyfr")
    @CfgComment("UPPERCASE_DIGITS - umozliwia uzycie duzych liter i cyfr")
    @CfgComment("LETTERS - umozliwia uzycie malych i duzych liter")
    @CfgComment("LETTERS_DIGITS - umozliwia uzycie malych i duzych liter oraz cyrf")
    @CfgComment("LETTERS_DIGITS_UNDERSCORE - umozliwia uzycie malych i duzych liter, cyrf oraz podkreslnika")
    @CfgName("name-regex")
    @CfgStringStyle(StringStyle.ALWAYS_QUOTED)
    public String nameRegex_ = "LETTERS";

    @CfgExclude
    public GuildRegex nameRegex;

    @CfgComment("Zasada sprawdzania tagu gildii przy jej tworzeniu")
    @CfgComment("Mozliwe zasady sa takie same jak w przypadku name-regex")
    @CfgName("tag-regex")
    @CfgStringStyle(StringStyle.ALWAYS_QUOTED)
    public String tagRegex_ = "LETTERS";

    @CfgExclude
    public GuildRegex tagRegex;

    @CfgComment("Minimalna liczba graczy w gildii, aby zaliczala sie ona do rankingu")
    @CfgName("guild-min-members")
    public int minMembersToInclude = 1;

    @CfgComment("Czy wiadomosci o braku potrzebnych przedmiotow maja zawierac elementy, na ktore mozna najechac")
    @CfgComment("Takie elementy pokazuja informacje o przedmiocie, np. jego typ, nazwe czy opis")
    @CfgComment("Funkcja jest obecnie troche niedopracowana i moze powodowac problemy na niektorych wersjach MC, np. 1.8.8")
    @CfgName("enable-item-component")
    public boolean enableItemComponent = false;

    @CfgComment("Przedmioty wymagane do zalozenia gildii")
    @CfgComment("Tylko wartosci ujete w <> sa wymagane, reszta (ujeta w []) jest opcjonalna")
    @CfgComment("Wzor: <ilosc> <przedmiot>:[metadata] [name:lore:enchant:eggtype:skullowner:armorcolor:flags]")
    @CfgComment("Przyklad: \"5 stone name:&bFunnyGuilds lore:&eJestem_najlepszym#&6pluginem!\"")
    @CfgComment("")
    @CfgComment("Zamiast spacji wstawiaj podkreslnik: _")
    @CfgComment("Aby zrobic nowa linie lore wstaw hash: #")
    @CfgComment("Aby w lore uzyc znaku # wstaw {HASH}")
    @CfgComment("")
    @CfgComment("eggtype to typ jajka do spawnu moba, uzywane tylko gdy typem przedmiotu jest MONSTER_EGG")
    @CfgComment("skullowner to nick gracza, ktorego glowa jest tworzona, uzywane tylko gdy typem przedmiotu jest SKULL_ITEM")
    @CfgComment("armorcolor to kolor, w ktorym bedzie przedmiot, uzywane tylko gdy przedmiot jest czescia zbroi skorzanej")
    @CfgComment("flags to flagi, ktore maja byc nalozone na przedmiot. Dostepne flagi: HIDE_ENCHANTS, HIDE_ATTRIBUTES, HIDE_UNBREAKABLE, HIDE_DESTROYS, HIDE_PLACED_ON, HIDE_POTION_EFFECTS")
    @CfgComment("Kolor musi byc podany w postaci: \"R_G_B\"")
    @CfgComment("")
    @CfgComment(
            "UWAGA: Nazwy przedmiotow musza pasowac do nazw podanych tutaj: https://spigotdocs.okaeri.eu/select/org/bukkit/Material.html")
    @CfgComment(
            "UWAGA: Typ jajka musi pasowac do typow entity podanych tutaj: https://spigotdocs.okaeri.eu/select/org/bukkit/entity/EntityType.html")
    @CfgName("items")
    @CfgStringStyle(StringStyle.ALWAYS_QUOTED)
    @CfgCollectionStyle(CollectionStyle.ALWAYS_NEW_LINE)
    public List<String> items_ = Arrays.asList("5 stone", "5 dirt", "5 tnt");

    @CfgExclude
    public List<ItemStack> createItems;

    @CfgComment("Wymagana ilosc doswiadczenia do zalozenia gildii")
    @CfgName("required-experience")
    public int requiredExperience = 0;

    @CfgComment("Wymagana ilosc pieniedzy do zalozenia gildii")
    @CfgComment("UWAGA: Aby ta opcja mogla dzialac, na serwerze musi byc plugin Vault oraz plugin dodajacy ekonomie")
    @CfgName("required-money")
    public double requiredMoney = 0;

    @CfgComment("Przedmioty wymagane do zalozenia gildii dla osoby z uprawnieniem funnyguilds.vip.items")
    @CfgName("items-vip")
    @CfgStringStyle(StringStyle.ALWAYS_QUOTED)
    @CfgCollectionStyle(CollectionStyle.ALWAYS_NEW_LINE)
    public List<String> itemsVip_ = Arrays.asList("1 gold_ingot");

    @CfgExclude
    public List<ItemStack> createItemsVip;

    @CfgComment("Wymagana ilosc doswiadczenia do zalozenia gildii dla osoby z uprawnieniem funnyguilds.vip.items")
    @CfgName("required-experience-vip")
    public int requiredExperienceVip = 0;

    @CfgComment("Wymagana ilosc pieniedzy do zalozenia gildii dla osoby z uprawnieniem funnyguilds.vip.items")
    @CfgComment("UWAGA: Aby ta opcja mogla dzialac, na serwerze musi byc plugin Vault oraz plugin dodajacy ekonomie")
    @CfgName("required-money-vip")
    public double requiredMoneyVip = 0;

    @CfgComment("Czy opcja wymaganego rankingu do zalozenia gildi ma byc wlaczona?")
    @CfgName("rank-create-enable")
    public boolean rankCreateEnable = true;

    @CfgComment("Minimalny ranking wymagany do zalozenia gildi")
    @CfgName("rank-create")
    public int rankCreate = 1000;

    @CfgComment("Minimalny ranking wymagany do zalozenia gildi dla osoby z uprawnieniem funnyguilds.vip.rank")
    @CfgName("rank-create-vip")
    public int rankCreateVip = 800;

    @CfgComment("Czy GUI z przedmiotami na gildie ma byc wspolne dla wszystkich?")
    @CfgComment(
            "Jesli wlaczone - wszyscy gracze beda widzieli GUI stworzone w sekcji gui-items, a GUI z sekcji gui-items-vip bedzie ignorowane")
    @CfgName("use-common-gui")
    public boolean useCommonGUI = false;

    @CfgComment("GUI z przedmiotami na gildie dla osob bez uprawnienia funnyguilds.vip.items")
    @CfgComment("Jesli wlaczone jest use-common-gui - ponizsze GUI jest uzywane takze dla osob z uprawnieniem funnyguilds.vip.items")
    @CfgComment(
            "Kazda linijka listy oznacza jeden slot, liczba slotow powinna byc wielokrotnoscia liczby 9 i nie powinna byc wieksza niz 54")
    @CfgComment(
            "Aby uzyc przedmiotu stworzonego w jednym slocie w innym mozna uzyc {GUI-nr}, np. {GUI-1} wstawi ten sam przedmiot, ktory jest w pierwszym slocie")
    @CfgComment("Aby wstawic przedmiot na gildie nalezy uzyc {ITEM-nr}, np. {ITEM-1} wstawi pierwszy przedmiot na gildie")
    @CfgComment("Aby wstawic przedmiot na gildie z listy vip nalezy uzyc {VIPITEM-nr}")
    @CfgName("gui-items")
    @CfgStringStyle(StringStyle.ALWAYS_QUOTED)
    @CfgCollectionStyle(CollectionStyle.ALWAYS_NEW_LINE)
    public List<String> guiItems_ = Arrays.asList("1 glass name:&r", "{GUI-1}", "{GUI-1}", "{GUI-1}", "{GUI-1}", "{GUI-1}", "{GUI-1}",
            "{GUI-1}", "{GUI-1}", "{GUI-1}", "1 paper name:&b&lItemy_na_gildie", "{GUI-1}", "{ITEM-1}", "{ITEM-2}", "{ITEM-3}", "{GUI-1}",
            "{GUI-11}", "{GUI-1}", "{GUI-1}", "{GUI-1}", "{GUI-1}", "{GUI-1}", "{GUI-1}", "{GUI-1}", "{GUI-1}", "{GUI-1}", "{GUI-1}");

    @CfgExclude
    public List<ItemStack> guiItems;

    @CfgComment("Nazwa GUI z przedmiotami na gildie dla osob bez uprawnienia funnyguilds.vip.items")
    @CfgComment("Nazwa moze zawierac max. 32 znaki (wliczajac w to kody kolorow)")
    @CfgName("gui-items-title")
    @CfgStringStyle(StringStyle.ALWAYS_QUOTED)
    public String guiItemsTitle_ = "&5&lPrzedmioty na gildie";

    @CfgExclude
    public String guiItemsTitle;

    @CfgComment("GUI z przedmiotami na gildie dla osob z uprawnieniem funnyguilds.vip.items")
    @CfgComment("Zasada tworzenia GUI jest taka sama jak w przypadku sekcji gui-items")
    @CfgComment("Ponizsze GUI bedzie ignorowane jesli wlaczone jest use-common-gui")
    @CfgName("gui-items-vip")
    @CfgStringStyle(StringStyle.ALWAYS_QUOTED)
    @CfgCollectionStyle(CollectionStyle.ALWAYS_NEW_LINE)
    public List<String> guiItemsVip_ = Arrays.asList("1 glass name:&r", "{GUI-1}", "{GUI-1}", "{GUI-1}", "{GUI-1}", "{GUI-1}", "{GUI-1}",
            "{GUI-1}", "{GUI-1}", "{GUI-1}", "1 paper name:&b&lItemy_na_gildie", "{GUI-1}", "{GUI-1}", "{VIPITEM-1}", "{GUI-3}", "{GUI-1}",
            "{GUI-11}", "{GUI-1}", "{GUI-1}", "{GUI-1}", "{GUI-1}", "{GUI-1}", "{GUI-1}", "{GUI-1}", "{GUI-1}", "{GUI-1}", "{GUI-1}");

    @CfgExclude
    public List<ItemStack> guiItemsVip;

    @CfgComment("Nazwa GUI z przedmiotami na gildie dla osob z uprawnieniem funnyguilds.vip.items")
    @CfgComment("Nazwa moze zawierac max. 32 znaki (wliczajac w to kody kolorow)")
    @CfgName("gui-items-vip-title")
    @CfgStringStyle(StringStyle.ALWAYS_QUOTED)
    public String guiItemsVipTitle_ = "&5&lPrzedmioty na gildie (VIP)";

    @CfgExclude
    public String guiItemsVipTitle;

    @CfgComment("Czy do przedmiotow na gildie, ktore sa w GUI, maja byc dodawane dodatkowe linie opisu?")
    @CfgComment("Linie te mozna ustawic ponizej")
    @CfgName("add-lore-lines")
    public boolean addLoreLines = true;

    @CfgComment("Dodatkowe linie opisu, dodawane do kazdego przedmiotu, ktory jest jednoczesnie przedmiotem na gildie")
    @CfgComment("Dodawane linie nie zaleza od otwieranego GUI - sa wspolne dla zwyklego i VIP")
    @CfgComment("Mozliwe do uzycia zmienne:")
    @CfgComment("{REQ-AMOUNT} - calkowita wymagana ilosc przedmiotu")
    @CfgComment("{PINV-AMOUNT} - ilosc danego przedmiotu, jaka gracz ma przy sobie")
    @CfgComment("{PINV-PERCENT} - procent wymaganej ilosci danego przedmiotu, jaki gracz ma przy sobie")
    @CfgComment("{EC-AMOUNT} - ilosc danego przedmiotu, jaka gracz ma w enderchescie")
    @CfgComment("{EC-PERCENT} - procent wymaganej ilosci danego przedmiotu, jaki gracz ma w enderchescie")
    @CfgComment("{ALL-AMOUNT} - ilosc danego przedmiotu, jaka gracz ma przy sobie i w enderchescie")
    @CfgComment("{ALL-PERCENT} - procent wymaganej ilosci danego przedmiotu, jaki gracz ma przy sobie i w enderchescie")
    @CfgName("gui-items-lore")
    @CfgStringStyle(StringStyle.ALWAYS_QUOTED)
    @CfgCollectionStyle(CollectionStyle.ALWAYS_NEW_LINE)
    public List<String> guiItemsLore_ = Arrays.asList("", "&aPosiadzasz juz:", "&a{PINV-AMOUNT} przy sobie &7({PINV-PERCENT}%)",
            "&a{EC-AMOUNT} w enderchescie &7({EC-PERCENT}%)", "&a{ALL-AMOUNT} calkowicie &7({ALL-PERCENT}%)");

    @CfgExclude
    public List<String> guiItemsLore;

    @CfgComment("Minimalna odleglosc od spawnu")
    @CfgName("create-distance")
    public int createDistance = 100;

    @CfgComment("Minimalna odleglosc od granicy mapy, na ktorej znajduje sie gracz")
    @CfgComment("Wartosc -1 oznacza brak minimalnej odlegosci od granicy")
    @CfgName("create-guild-min-distance")
    public double createMinDistanceFromBorder = - 1.0;

    @CfgComment("Blok lub entity, ktore jest sercem gildii")
    @CfgComment("Zmiana entity wymaga pelnego restartu serwera")
    @CfgComment("Bloki musza byc podawane w formacie - material:metadata")
    @CfgComment("Nazwy blokow musza pasowac do nazw podanych tutaj: https://spigotdocs.okaeri.eu/select/org/bukkit/Material.html")
    @CfgComment(
            "Typ entity musi byc zgodny z ta lista (i zdrowym rozsadkiem) - https://spigotdocs.okaeri.eu/select/org/bukkit/entity/EntityType.html")
    @CfgComment("UWAGA: Zmiana bloku, gdy sa juz zrobione jakies gildie, spowoduje niedzialanie ich regionow")
    @CfgComment(" ")
    @CfgComment(
            "UWAGA: Jesli jako serca gildii chcesz uzyc bloku, ktory spada pod wplywem grawitacji - upewnij sie, ze bedzie on stal na jakims bloku!")
    @CfgComment("Jesli pojawi sie w powietrzu - spadnie i plugin nie bedzie odczytywal go poprawnie!")
    @CfgName("create-type")
    @CfgStringStyle(StringStyle.ALWAYS_QUOTED)
    public String createType = "ender_crystal";

    @CfgExclude
    public Pair<Material, Byte> createMaterial;

    @CfgExclude
    public EntityType createEntityType;

    @CfgComment("Na jakim poziomie ma byc wyznaczone centrum gildii")
    @CfgComment("Wpisz 0 jesli ma byc ustalone przez pozycje gracza")
    @CfgName("create-center-y")
    public int createCenterY = 60;

    @CfgComment("Czy ma sie tworzyc kula z obsydianu dookola centrum gildii")
    @CfgName("create-center-sphere")
    public boolean createCenterSphere = true;

    @CfgComment("Czy przy tworzeniu gildii powinien byc wklejany schemat")
    @CfgComment("Wklejenie schematu wymaga pluginu WorldEdit")
    @CfgName("paste-schematic-on-creation")
    public boolean pasteSchematicOnCreation = false;

    @CfgComment("Nazwa pliku ze schematem poczatkowym gildii")
    @CfgComment("Wklejenie schematu wymaga pluginu WorldEdit")
    @CfgComment("Schemat musi znajdować się w folderze FunnyGuilds")
    @CfgName("guild-schematic-file-name")
    @CfgStringStyle(StringStyle.ALWAYS_QUOTED)
    public String guildSchematicFileName = "funnyguilds.schematic";

    @CfgComment("Czy schemat przy tworzeniu gildii powinien byc wklejany razem z powietrzem?")
    @CfgComment("Przy duzych schematach ma to wplyw na wydajnosc")
    @CfgComment("Wklejenie schematu wymaga pluginu WorldEdit")
    @CfgName("paste-schematic-with-air")
    public boolean pasteSchematicWithAir = true;

    @CfgExclude
    public File guildSchematicFile;

    @CfgComment("Typy blokow, z ktorymi osoba spoza gildii NIE moze prowadzic interakcji na terenie innej gildii")
    @CfgName("blocked-interact")
    @CfgStringStyle(StringStyle.ALWAYS_QUOTED)
    @CfgCollectionStyle(CollectionStyle.ALWAYS_NEW_LINE)
    public List<String> _blockedInteract = Arrays.asList("CHEST", "TRAPPED_CHEST");

    @CfgExclude
    public Set<Material> blockedInteract;

    @CfgComment("Czy funkcja efektu 'zbugowanych' klockow ma byc wlaczona (dziala tylko na terenie wrogiej gildii)")
    @CfgName("bugged-blocks")
    public boolean buggedBlocks = false;

    @CfgComment("Czas po ktorym 'zbugowane' klocki maja zostac usuniete")
    @CfgComment("Czas podawany w tickach (1 sekunda = 20 tickow)")
    @CfgName("bugged-blocks-timer")
    public long buggedBlocksTimer = 20L;

    @CfgComment("Bloki, ktorych nie mozna 'bugowac'")
    @CfgComment("Nazwy blokow musza pasowac do nazw podanych tutaj: https://spigotdocs.okaeri.eu/select/org/bukkit/Material.html")
    @CfgName("bugged-blocks-exclude")
    @CfgStringStyle(StringStyle.ALWAYS_QUOTED)
    @CfgCollectionStyle(CollectionStyle.ALWAYS_NEW_LINE)
    public List<String> buggedBlocksExclude_ = Arrays.asList(
            // Ban basic
            "TNT", "STATIONARY_LAVA", "STATIONARY_WATER",
            // Ban TNT Minecart placement
            "RAILS", "DETECTOR_RAIL", "ACTIVATOR_RAIL", "POWERED_RAIL",
            // Ban gravity blocks that won't be removed when fallen
            "ANVIL", "GRAVEL", "SAND", "DRAGON_EGG",
            // Ban pistons and other components that may produce redstone output or interact with it
            "PISTON_BASE", "PISTON_STICKY_BASE",
            "REDSTONE_BLOCK", "REDSTONE_TORCH_ON", "REDSTONE_TORCH_OFF", "DIODE", "REDSTONE_COMPARATOR", "DAYLIGHT_DETECTOR",
            "DISPENSER", "HOPPER", "DROPPER", "OBSERVER",
            "STONE_PLATE", "WOOD_PLATE", "GOLD_PLATE", "IRON_PLATE", "LEVER", "TRIPWIRE_HOOK", "TRAP_DOOR", "IRON_TRAPDOOR", "WOOD_BUTTON", "STONE_BUTTON",
            "WOOD_DOOR", "IRON_DOOR", "SPRUCE_DOOR_ITEM", "BIRCH_DOOR_ITEM", "JUNGLE_DOOR_ITEM", "ACACIA_DOOR_ITEM", "DARK_OAK_DOOR_ITEM",
            "FENCE_GATE", "SPRUCE_FENCE_GATE", "JUNGLE_FENCE_GATE", "DARK_OAK_FENCE_GATE", "BIRCH_FENCE_GATE",
            "REDSTOE_LAMP_ON", "REDSTOE_LAMP_OFF",
            "TRAPPED_CHEST", "CHEST"
            );

    @CfgExclude
    public Set<Material> buggedBlocksExclude;

    @CfgComment("Czy klocki po 'zbugowaniu' maja zostac oddane")
    @CfgName("bugged-blocks-return")
    public boolean buggedBlockReturn = false;

    @CfgComment("Maksymalna liczba czlonkow w gildii")
    @CfgName("max-members")
    public int maxMembersInGuild = 15;

    @CfgComment("Maksymalna liczba sojuszy miedzy gildiami")
    @CfgName("max-allies")
    public int maxAlliesBetweenGuilds = 15;

    @CfgComment("Lista nazw swiatow, na ktorych mozliwosc utworzenia gildii powinna byc zablokowana")
    @CfgName("blocked-worlds")
    @CfgStringStyle(StringStyle.ALWAYS_QUOTED)
    @CfgCollectionStyle(CollectionStyle.ALWAYS_NEW_LINE)
    public List<String> blockedWorlds = Collections.singletonList("some_world");

    @CfgComment("Mozliwosc ucieczki z terenu innej gildii")
    @CfgComment("Funkcja niedostepna jesli mozliwosc teleportacji do gildii jest wylaczona")
    @CfgName("escape-enable")
    public boolean escapeEnable = true;

    @CfgComment("Czas, w sekundach, jaki musi uplynac od wlaczenia ucieczki do teleportacji")
    @CfgName("escape-delay")
    public int escapeDelay = 120;

    @CfgComment("Mozliwosc teleportacji do gildii")
    @CfgName("base-enable")
    public boolean baseEnable = true;

    @CfgComment("Czas oczekiwania na teleportacje (w sekundach)")
    @CfgName("base-delay")
    public int baseDelay = 5;

    @CfgComment("Czas oczekiwania na teleportacje (w sekundach) dla graczy posiadajacych uprawnienie (funnyguilds.vip.baseTeleportTime)")
    @CfgName("base-delay-vip")
    public int baseDelayVip = 3;

    @CfgComment("Koszt teleportacji do gildii (jezeli brak, wystarczy zrobic: base-items: [])")
    @CfgName("base-items")
    @CfgStringStyle(StringStyle.ALWAYS_QUOTED)
    @CfgCollectionStyle(CollectionStyle.ALWAYS_NEW_LINE)
    public List<String> baseItems_ = Arrays.asList("1 diamond", "1 emerald");

    @CfgExclude
    public List<ItemStack> baseItems;

    @CfgComment("Koszt dolaczenia do gildii (jezeli brak, wystarczy zrobic: join-items: [])")
    @CfgName("join-items")
    @CfgStringStyle(StringStyle.ALWAYS_QUOTED)
    @CfgCollectionStyle(CollectionStyle.ALWAYS_NEW_LINE)
    public List<String> joinItems_ = Collections.singletonList("1 diamond");

    @CfgExclude
    public List<ItemStack> joinItems;

    @CfgComment("Mozliwosc powiekszania gildii")
    @CfgName("enlarge-enable")
    public boolean enlargeEnable = true;

    @CfgComment("O ile powieksza teren gildii 1 poziom")
    @CfgName("enlarge-size")
    public int enlargeSize = 5;

    @CfgComment("Koszt powiekszania gildii")
    @CfgComment("- kazdy myslnik, to 1 poziom gildii")
    @CfgName("enlarge-items")
    @CfgStringStyle(StringStyle.ALWAYS_QUOTED)
    @CfgCollectionStyle(CollectionStyle.ALWAYS_NEW_LINE)
    public List<String> enlargeItems_ = Arrays.asList("8 diamond", "16 diamond", "24 diamond", "32 diamond", "40 diamond", "48 diamond", "56 diamond", "64 diamond", "72 diamond", "80 diamond");

    @CfgExclude
    public List<ItemStack> enlargeItems;

    @CfgComment("Wielkosc regionu gildii")
    @CfgName("region-size")
    public int regionSize = 50;

    @CfgComment("Minimalna odleglosc miedzy terenami gildii")
    @CfgName("region-min-distance")
    public int regionMinDistance = 10;

    @CfgComment("Czas wyswietlania powiadomienia na pasku powiadomien (w sekundach)")
    @CfgName("region-notification-time")
    public int regionNotificationTime = 15;

    @CfgComment("Co ile moze byc wywolywany pasek powiadomien przez jednego gracza (w sekundach)")
    @CfgName("region-notification-cooldown")
    public int regionNotificationCooldown = 60;

    @CfgComment("Blokowane komendy dla graczy spoza gildii na jej terenie")
    @CfgName("region-commands")
    @CfgStringStyle(StringStyle.ALWAYS_QUOTED)
    @CfgCollectionStyle(CollectionStyle.ALWAYS_NEW_LINE)
    public List<String> regionCommands = Collections.singletonList("sethome");

    @CfgComment("Czy proces usuniecia gildii powinien zostac przerwany jezeli ktos spoza gildii jest na jej terenie")
    @CfgName("guild-delete-cancel-if-someone-is-on-region")
    public boolean guildDeleteCancelIfSomeoneIsOnRegion = false;

    @CfgComment("Czy wlaczyc ochrone przed TNT w gildiach w podanych godzinach")
    @CfgName("guild-tnt-protection-enabled")
    public boolean guildTNTProtectionEnabled = false;

    @CfgComment("O której godzinie ma sie zaczac ochrona przed TNT w gildii")
    @CfgComment("Godzina w formacie HH:mm")
    @CfgName("guild-tnt-protection-start-time")
    @CfgStringStyle(StringStyle.ALWAYS_QUOTED)
    public String guildTNTProtectionStartTime_ = "22:00";

    @CfgExclude
    public LocalTime guildTNTProtectionStartTime;

    @CfgComment("Do której godziny ma dzialac ochrona przed TNT w gildii")
    @CfgComment("Godzina w formacie HH:mm")
    @CfgName("guild-tnt-protection-end-time")
    @CfgStringStyle(StringStyle.ALWAYS_QUOTED)
    public String guildTNTProtectionEndTime_ = "06:00";

    @CfgExclude
    public LocalTime guildTNTProtectionEndTime;

    @CfgExclude
    public boolean guildTNTProtectionOrMode;

    @CfgComment("Przez ile sekund nie mozna budowac na terenie gildii po wybuchu")
    @CfgName("region-explode")
    public int regionExplode = 120;

    @CfgComment("Jaki ma byc zasieg pobieranych przedmiotow po wybuchu (jezeli chcesz wylaczyc, wpisz 0)")
    @CfgName("explode-radius")
    public int explodeRadius = 3;

    @CfgComment("Jakie materialy i z jaka szansa maja byc niszczone po wybuchu")
    @CfgComment("<material>: <szansa (w %)")
    @CfgComment("Jeżeli wszystkie materialy maja miec okreslony % na wybuch, uzyj specjalnego znaku '*'")
    @CfgName("explode-materials")
    public Map<String, Double> explodeMaterials_ = ImmutableMap.of(
            "ender_chest", 20.0,
            "enchantment_table", 20.0,
            "obsidian", 20.0,
            "water", 33.0,
            "lava", 33.0);

    @CfgExclude
    public Map<Material, Double> explodeMaterials;

    @CfgExclude
    public boolean allMaterialsAreExplosive;

    @CfgExclude
    public double defaultExplodeChance = -1.0;

    @CfgComment("Czy powstale wybuchy powinny niszczyc bloki wylacznie na terenach gildii")
    @CfgName("explode-should-affect-only-guild")
    public boolean explodeShouldAffectOnlyGuild = false;

    @CfgComment("Ile zyc ma gildia")
    @CfgName("war-lives")
    public int warLives = 3;

    @CfgComment("Po jakim czasie od zalozenia mozna zaatakowac gildie")
    @CfgName("war-protection")
    @CfgStringStyle(StringStyle.ALWAYS_QUOTED)
    public String warProtection_ = "24h";

    @CfgExclude
    public long warProtection;

    @CfgComment("Ile czasu trzeba czekac do nastepnego ataku na gildie")
    @CfgName("war-wait")
    @CfgStringStyle(StringStyle.ALWAYS_QUOTED)
    public String warWait_ = "24h";

    @CfgExclude
    public long warWait;

    @CfgComment("Czy gildia podczas okresu ochronnego ma posiadac ochrone przeciw TNT")
    @CfgName("war-tnt-protection")
    public boolean warTntProtection = true;

    @CfgComment("Jaka waznosc ma gildia po jej zalozeniu")
    @CfgName("validity-start")
    @CfgStringStyle(StringStyle.ALWAYS_QUOTED)
    public String validityStart_ = "14d";

    @CfgExclude
    public long validityStart;

    @CfgComment("Ile czasu dodaje przedluzenie gildii")
    @CfgName("validity-time")
    @CfgStringStyle(StringStyle.ALWAYS_QUOTED)
    public String validityTime_ = "14d";

    @CfgExclude
    public long validityTime;

    @CfgComment("Ile dni przed koncem wygasania mozna przedluzyc gildie (wpisz 0, jezeli funkcja ma byc wylaczona)")
    @CfgName("validity-when")
    @CfgStringStyle(StringStyle.ALWAYS_QUOTED)
    public String validityWhen_ = "14d";

    @CfgExclude
    public long validityWhen;

    @CfgComment("Koszt przedluzenia gildii")
    @CfgName("validity-items")
    @CfgStringStyle(StringStyle.ALWAYS_QUOTED)
    @CfgCollectionStyle(CollectionStyle.ALWAYS_NEW_LINE)
    public List<String> validityItems_ = Collections.singletonList("10 diamond");

    @CfgExclude
    public List<ItemStack> validityItems;

    @CfgComment("Czy wiadomosc o zabiciu gracza powinna byc pokazywana wszystkim")
    @CfgComment("Jesli wylaczone - bedzie pokazywana tylko graczom, ktorzy brali udzial w walce")
    @CfgName("broadcast-death-message")
    public boolean broadcastDeathMessage = true;

    @CfgComment("Czy wiadomosc o zabiciu gracza powinna byc wyswietlana bez wzgledu na wylaczone wiadomosci o smierci")
    @CfgName("ignore-death-messages-disabled")
    public boolean ignoreDisabledDeathMessages = false;

    @CfgComment("Ranking od ktorego rozpoczyna gracz")
    @CfgName("rank-start")
    public int rankStart = 1000;

    @CfgComment("Czy blokada nabijania rankingu na tych samych osobach powinna byc wlaczona")
    @CfgName("rank-farming-protect")
    public boolean rankFarmingProtect = true;

    @CfgComment("Czy ostatni gracz, ktory zaatakowal gracza, ktory zginal ma byc uznawany jako zabojca")
    @CfgName("rank-farming-last-attacker-as-killer")
    public boolean considerLastAttackerAsKiller = false;

    @CfgComment("Przez ile sekund gracz, ktory zaatakowal gracza, ktory zginal ma byc uznawany jako zabojca")
    @CfgName("rank-farming-consideration-timeout")
    public int lastAttackerAsKillerConsiderationTimeout = 30;

    @CfgExclude
    public long lastAttackerAsKillerConsiderationTimeout_;

    @CfgComment("Czas (w sekundach) blokady nabijania rankingu po walce dwoch osob")
    @CfgName("rank-farming-cooldown")
    public int rankFarmingCooldown = 7200;

    @CfgComment("Czy ma byc zablokowana zmiana rankingu, jesli obie osoby z walki maja taki sam adres IP")
    @CfgName("rank-ip-protect")
    public boolean rankIPProtect = false;

    @CfgComment("Czy gracze z uprawnieniem 'funnyguilds.ranking.exempt' powinni byc uwzglednieni przy wyznaczaniu pozycji gracza w rankingu")
    @CfgName("skip-privileged-players-in-rank-positions")
    public boolean skipPrivilegedPlayersInRankPositions = false;

    @CfgComment("Co ile ticków ranking graczy oraz gildii powinien zostać odświeżony")
    @CfgName("ranking-update-interval")
    public int rankingUpdateInterval = 40;

    @CfgExclude
    public long rankingUpdateInterval_;

    @CfgComment("Czy system asyst ma byc wlaczony")
    @CfgName("rank-assist-enable")
    public boolean assistEnable = true;

    @CfgComment("Limit asyst (liczba ujemna = wylaczony)")
    @CfgName("assists-limit")
    public int assistsLimit = - 1;

    @CfgComment("Jaka czesc rankingu za zabicie idzie na konto zabojcy")
    @CfgComment("1 to caly ranking, 0 to nic")
    @CfgComment("Reszta rankingu rozdzielana jest miedzy osoby asystujace w zaleznosci od zadanych obrazen")
    @CfgName("rank-assist-killer-share")
    public double assistKillerShare = 0.5;

    @CfgComment("Na jakich regionach ma ignorowac nadawanie asyst")
    @CfgComment("UWAGA: Wymagany plugin WorldGuard")
    @CfgName("assists-regions-ignored")
    @CfgStringStyle(StringStyle.ALWAYS_QUOTED)
    @CfgCollectionStyle(CollectionStyle.ALWAYS_NEW_LINE)
    public List<String> assistsRegionsIgnored = Collections.singletonList("spawnGuildHeart");

    @CfgComment("System rankingowy uzywany przez plugin, do wyboru:")
    @CfgComment("ELO - system bazujacy na rankingu szachowym ELO, najlepiej zbalansowany ze wszystkich trzech")
    @CfgComment("PERCENT - system, ktory obu graczom zabiera procent rankingu osoby zabitej")
    @CfgComment("STATIC - system, ktory zawsze zabiera iles rankingu zabijajacemu i iles zabitemu")
    @CfgName("rank-system")
    @CfgStringStyle(StringStyle.ALWAYS_QUOTED)
    public String rankSystem_ = "ELO";

    @CfgExclude
    public RankSystem rankSystem;

    @CfgComment("Sekcja uzywana TYLKO jesli wybranym rank-system jest ELO!")
    @CfgComment(
            "Lista stalych obliczen rankingowych ELO, uzywanych przy zmianach rankingu - im mniejsza stala, tym mniejsze zmiany rankingu")
    @CfgComment("Stale okreslaja tez o ile maksymalnie moze zmienic sie ranking pochodzacy z danego przedzialu")
    @CfgComment("Lista powinna byc podana od najmniejszych do najwiekszych rankingow i zawierac tylko liczby naturalne, z zerem wlacznie")
    @CfgComment("Elementy listy powinny byc postaci: \"minRank-maxRank stala\", np.: \"0-1999 32\"")
    @CfgComment("* uzyta w zapisie elementu listy oznacza wszystkie wartosci od danego minRank w gore, np.: \"2401-* 16\"")
    @CfgName("elo-constants")
    @CfgStringStyle(StringStyle.ALWAYS_QUOTED)
    @CfgCollectionStyle(CollectionStyle.ALWAYS_NEW_LINE)
    public List<String> eloConstants_ = Arrays.asList("0-1999 32", "2000-2400 24", "2401-* 16");

    @CfgExclude
    public Map<IntegerRange, Integer> eloConstants;

    @CfgComment("Sekcja uzywana TYLKO jesli wybranym rank-system jest ELO!")
    @CfgComment("Dzielnik obliczen rankingowych ELO - im mniejszy, tym wieksze zmiany rankingu")
    @CfgComment("Dzielnik powinien byc liczba dodatnia, niezerowa")
    @CfgName("elo-divider")
    public double eloDivider = 400.0D;

    @CfgComment("Sekcja uzywana TYLKO jesli wybranym rank-system jest ELO!")
    @CfgComment("Wykladnik potegi obliczen rankingowych ELO - im mniejszy, tym wieksze zmiany rankingu")
    @CfgComment("Wykladnik powinien byc liczba dodatnia, niezerowa")
    @CfgName("elo-exponent")
    public double eloExponent = 10.0D;

    @CfgComment("Sekcja uzywana TYLKO jesli wybranym rank-system jest PERCENT!")
    @CfgComment("Procent rankingu osoby zabitej o jaki zmienia sie rankingi po walce")
    @CfgName("percent-rank-change")
    public double percentRankChange = 1.0;

    @CfgComment("Sekcja uzywana TYLKO jesli wybranym rank-system jest STATIC!")
    @CfgComment("Punkty dawane osobie, ktora wygrywa walke")
    @CfgName("static-attacker-change")
    public int staticAttackerChange = 15;

    @CfgComment("Sekcja uzywana TYLKO jesli wybranym rank-system jest STATIC!")
    @CfgComment("Punkty zabierane osobie, ktora przegrywa walke")
    @CfgName("static-victim-change")
    public int staticVictimChange = 10;

    @CfgComment("Czy pokazywac informacje przy kliknieciu PPM na gracza")
    @CfgName("info-player-enabled")
    public boolean infoPlayerEnabled = true;

    @CfgComment("Czy pokazac informacje z komendy /gracz przy kliknieciu PPM")
    @CfgComment("Jesli wylaczone - pokazywane beda informacje z sekcji \"playerRightClickInfo\" z messages.yml")
    @CfgName("info-player-command")
    public boolean infoPlayerCommand = true;

    @CfgComment("Cooldown pomiedzy pokazywaniem informacji przez PPM (w sekundach)")
    @CfgName("info-player-cooldown")
    public int infoPlayerCooldown = 5;

    @CfgComment("Czy trzeba kucac, zeby przy klikniecu PPM na gracza wyswietlilo informacje o nim")
    @CfgName("info-player-sneaking")
    public boolean infoPlayerSneaking = true;

    @CfgComment("Czy czlonkowie gildii moga sobie zadawac obrazenia (domyslnie)")
    @CfgName("damage-guild")
    public boolean damageGuild = false;

    @CfgComment("Czy sojuszniczy moga sobie zadawac obrazenia")
    @CfgName("damage-ally")
    public boolean damageAlly = false;

    @CfgComment("Wyglad znaczika {POS} wstawionego w format chatu")
    @CfgComment("Znacznik ten pokazuje czy ktos jest liderem, zastepca czy zwyklym czlonkiem gildii")
    @CfgName("chat-position")
    @CfgStringStyle(StringStyle.ALWAYS_QUOTED)
    public String chatPosition_ = "&b{POS} ";

    @CfgExclude
    public String chatPosition;

    @CfgComment("Znacznik dla lidera gildii")
    @CfgName("chat-position-leader")
    @CfgStringStyle(StringStyle.ALWAYS_QUOTED)
    public String chatPositionLeader = "**";

    @CfgComment("Znacznik dla zastepcy gildii")
    @CfgName("chat-position-deputy")
    @CfgStringStyle(StringStyle.ALWAYS_QUOTED)
    public String chatPositionDeputy = "*";

    @CfgComment("Znacznik dla czlonka gildii")
    @CfgName("chat-position-member")
    @CfgStringStyle(StringStyle.ALWAYS_QUOTED)
    public String chatPositionMember = "";

    @CfgComment("Wyglad znaczika {TAG} wstawionego w format chatu")
    @CfgName("chat-guild")
    @CfgStringStyle(StringStyle.ALWAYS_QUOTED)
    public String chatGuild_ = "&b{TAG} ";

    @CfgExclude
    public String chatGuild;

    @CfgComment("Wyglad znaczika {RANK} wstawionego w format chatu")
    @CfgName("chat-rank")
    @CfgStringStyle(StringStyle.ALWAYS_QUOTED)
    public String chatRank_ = "&b{RANK} ";

    @CfgExclude
    public String chatRank;

    @CfgComment("Wyglad znaczika {POINTS} wstawionego w format chatu")
    @CfgComment("Mozesz tu takze uzyc znacznika {POINTS-FORMAT}")
    @CfgName("chat-points")
    @CfgStringStyle(StringStyle.ALWAYS_QUOTED)
    public String chatPoints_ = "&b{POINTS} ";

    @CfgExclude
    public String chatPoints;

    @CfgComment("Wyglad znacznika {POINTS-FORMAT} i {G-POINTS-FORMAT} w zaleznosci od wartosci punktow")
    @CfgComment("{G-POINTS-FORMAT} (tak samo jak {G-POINTS}) jest uzywane jedynie na liscie graczy")
    @CfgComment("Lista powinna byc podana od najmniejszych do najwiekszych rankingow i zawierac tylko liczby naturalne, z zerem wlacznie")
    @CfgComment("Elementy listy powinny byc postaci: \"minRank-maxRank wyglad\", np.: \"0-750 &4{POINTS}\"")
    @CfgComment("Pamietaj, aby kazdy mozliwy ranking mial ustalony format!")
    @CfgComment("* uzyta w zapisie elementu listy oznacza wszystkie wartosci od danego minRank w gore, np.: \"1500-* &6&l{POINTS}\"")
    @CfgName("points-format")
    @CfgStringStyle(StringStyle.ALWAYS_QUOTED)
    @CfgCollectionStyle(CollectionStyle.ALWAYS_NEW_LINE)
    public List<String> pointsFormat_ = Arrays.asList("0-749 &4{POINTS}", "750-999 &c{POINTS}", "1000-1499 &a{POINTS}", "1500-* &6&l{POINTS}");

    @CfgExclude
    public Map<IntegerRange, String> pointsFormat;

    @CfgComment("Znacznik z punktami dodawany do zmiennej {PTOP-x} i {ONLINE-PTOP-x}")
    @CfgComment("Uzywaj zmiennych {POINTS} i {POINTS-FORMAT}")
    @CfgComment("Jesli nie chcesz wyswietlac punktow, tylko sam nick - nie podawaj tu nic")
    @CfgName("ptop-points")
    @CfgStringStyle(StringStyle.ALWAYS_QUOTED)
    public String ptopPoints_ = " &7[{POINTS}&7]";

    @CfgExclude
    public String ptopPoints;

    @CfgComment("Znacznik z punktami dodawany do zmiennej {GTOP-x}")
    @CfgComment("Uzywaj zmiennych {POINTS} i {POINTS-FORMAT}")
    @CfgComment("Jesli nie chcesz wyswietlac punktow, tylko sam tag - nie podawaj tu nic")
    @CfgName("gtop-points")
    @CfgStringStyle(StringStyle.ALWAYS_QUOTED)
    public String gtopPoints_ = " &7[&b{POINTS-FORMAT}&7]";

    @CfgExclude
    public String gtopPoints;

    @CfgComment("Wyglad znacznika {PING-FORMAT} w zaleznosci od wartosci pingu")
    @CfgComment("Lista powinna byc podana od najmniejszych do najwiekszych wartosci i zawierac tylko liczby naturalne, z zerem wlacznie")
    @CfgComment("Elementy listy powinny byc postaci: \"minPing-maxPing wyglad\", np.: \"0-75 &a{PING}\"")
    @CfgComment("* uzyta w zapisie elementu listy oznacza wszystkie wartosci od danego minPing w gore, np.: \"301-* &c{PING}\"")
    @CfgName("ping-format")
    @CfgStringStyle(StringStyle.ALWAYS_QUOTED)
    @CfgCollectionStyle(CollectionStyle.ALWAYS_NEW_LINE)
    public List<String> pingFormat_ = Arrays.asList("0-75 &a{PING}", "76-150 &e{PING}", "151-300 &c{PING}", "301-* &c{PING}");

    @CfgExclude
    public Map<IntegerRange, String> pingFormat;

    @CfgComment("Symbol od ktorego zaczyna sie wiadomosc do gildii gildii")
    @CfgName("chat-priv")
    @CfgStringStyle(StringStyle.ALWAYS_QUOTED)
    public String chatPriv = "!";

    @CfgComment("Symbol od ktorego zaczyna sie wiadomosc do sojusznikow gildii")
    @CfgName("chat-ally")
    @CfgStringStyle(StringStyle.ALWAYS_QUOTED)
    public String chatAlly = "!!";

    @CfgComment("Symbol od ktorego zaczyna sie wiadomosc do wszystkich gildii")
    @CfgName("chat-global")
    @CfgStringStyle(StringStyle.ALWAYS_QUOTED)
    public String chatGlobal = "!!!";

    @CfgComment("Wyglad wiadomosci wysylanej na czacie gildii")
    @CfgComment("Zmienne: {PLAYER}, {TAG}, {MESSAGE}, {POS}")
    @CfgName("chat-priv-design")
    @CfgStringStyle(StringStyle.ALWAYS_QUOTED)
    public String chatPrivDesign_ = "&8[&aChat gildii&8] &7{POS}{PLAYER}&8:&f {MESSAGE}";

    @CfgExclude
    public String chatPrivDesign;

    @CfgComment("Wyglad wiadomosci wysylanej na czacie sojusznikow dla sojusznikow")
    @CfgComment("Zmienne: {PLAYER}, {TAG}, {MESSAGE}, {POS}")
    @CfgName("chat-ally-design")
    @CfgStringStyle(StringStyle.ALWAYS_QUOTED)
    public String chatAllyDesign_ = "&8[&6Chat sojuszniczy&8] &8{TAG} &7{POS}{PLAYER}&8:&f {MESSAGE}";

    @CfgExclude
    public String chatAllyDesign;

    @CfgComment("Wyglad wiadomosci wysylanej na czacie globalnym gildii")
    @CfgComment("Zmienne: {PLAYER}, {TAG}, {MESSAGE}, {POS}")
    @CfgName("chat-global-design")
    @CfgStringStyle(StringStyle.ALWAYS_QUOTED)
    public String chatGlobalDesign_ = "&8[&cChat globalny gildii&8] &8{TAG} &7{POS}{PLAYER}&8:&f {MESSAGE}";

    @CfgExclude
    public String chatGlobalDesign;

    @CfgComment("Wyglad tagu osob w gildii")
    @CfgName("prefix-our")
    @CfgStringStyle(StringStyle.ALWAYS_QUOTED)
    public String prefixOur_ = "&a{TAG}&f ";

    @CfgExclude
    public String prefixOur;

    @CfgComment("Wyglad tagu gildii sojuszniczej")
    @CfgName("prefix-allies")
    @CfgStringStyle(StringStyle.ALWAYS_QUOTED)
    public String prefixAllies_ = "&6{TAG}&f ";

    @CfgExclude
    public String prefixAllies;

    @CfgComment("Wyglad tagu gildii neutralnej (widziany rowniez przez graczy bez gildii)")
    @CfgName("prefix-other")
    @CfgStringStyle(StringStyle.ALWAYS_QUOTED)
    public String prefixOther_ = "&7{TAG}&f ";

    @CfgExclude
    public String prefixOther;

    @CfgComment("Kolory dodawane przed nickiem gracza online przy zamianie zmiennej {PTOP-x}")
    @CfgComment("Jesli nie chcesz kolorowania zaleznego od statusu online - pozostaw te sekcje (i ptop-offline) pusta")
    @CfgName("ptop-online")
    @CfgStringStyle(StringStyle.ALWAYS_QUOTED)
    public String ptopOnline_ = "&a";

    @CfgExclude
    public String ptopOnline;

    @CfgComment("Kolory dodawane przed nickiem gracza offline przy zamianie zmiennej {PTOP-x}")
    @CfgComment("Jesli nie chcesz kolorowania zaleznego od statusu online - pozostaw te sekcje (i ptop-online) pusta")
    @CfgName("ptop-offline")
    @CfgStringStyle(StringStyle.ALWAYS_QUOTED)
    public String ptopOffline_ = "&c";

    @CfgExclude
    public String ptopOffline;

    @CfgName("use-shared-scoreboard")
    @CfgComment("Czy FunnyGuilds powinno korzystac z wspoldzielonego scoreboarda")
    @CfgComment("Ta opcja pozwala na dzialanie pluginu FunnyGuilds oraz innych pluginow modyfikujacych scoreboard ze soba")
    @CfgComment("UWAGA: Opcja eksperymentalna i moze powodowac bledy przy wyswietlaniu rzeczy zaleznych od scoreboardow!")
    public boolean useSharedScoreboard = false;

    @CfgComment("Czy wlaczyc dummy z punktami")
    @CfgComment(
            "UWAGA - zalecane jest wylaczenie tej opcji w przypadku konfliktow z BungeeCord'em, wiecej szczegolow tutaj: https://github.com/FunnyGuilds/FunnyGuilds/issues/769")
    @CfgName("dummy-enable")
    public boolean dummyEnable = true;

    @CfgComment("Wyglad nazwy wyswietlanej (suffix, za punktami)")
    @CfgName("dummy-suffix")
    @CfgStringStyle(StringStyle.ALWAYS_QUOTED)
    public String dummySuffix_ = "pkt";

    @CfgExclude
    public String dummySuffix;

    @CfgComment("Wyglad listy graczy, przedzial slotow - od 1 do 80")
    @CfgComment("Schemat wygladu listy: https://github.com/FunnyGuilds/FunnyGuilds/blob/master/assets/tab-scheme.png")
    @CfgComment("> Spis zmiennych gracza:")
    @CfgComment("{PLAYER} - nazwa gracza")
    @CfgComment("{WORLD} - swiat, w ktorym znajduje sie gracz")
    @CfgComment("{PING} - ping gracza")
    @CfgComment("{PING-FORMAT} - ping gracza z formatowaniem")
    @CfgComment("{POINTS} - punkty gracza")
    @CfgComment("{POINTS-FORMAT} - punkty gracza z formatowaniem")
    @CfgComment("{POSITION} - pozycja gracza w rankingu")
    @CfgComment("{KILLS} - liczba zabojstw gracza")
    @CfgComment("{DEATHS} - liczba smierci gracza")
    @CfgComment("{KDR} - stosunek zabojstw do smierci gracza")
    @CfgComment("{WG-REGION} - region WorldGuard'a, na ktorym znajduje sie gracz (pierwszy, jesli jest ich kilka)")
    @CfgComment("{WG-REGIONS} - regiony WorldGuard'a, na ktorych znajduje sie gracz (oddzielone przecinkami)")
    @CfgComment("> Spis zmiennych gildyjnych:")
    @CfgComment("{G-NAME} - nazwa gildii do ktorej nalezy gracz")
    @CfgComment("{G-TAG} - tag gildii gracza")
    @CfgComment("{G-OWNER} - wlasciciel gildii")
    @CfgComment("{G-DEPUTIES} - zastepcy gildii")
    @CfgComment("{G-DEPUTY} - losowy z zastepcow gildii")
    @CfgComment("{G-LIVES} - liczba zyc gildii")
    @CfgComment("{G-ALLIES} - liczba sojusznikow gildii")
    @CfgComment("{G-POINTS} - punkty gildii")
    @CfgComment("{G-POINTS-FORMAT} - punkty gildii z formatowaniem")
    @CfgComment("{G-POSITION} - pozycja gildii gracza w rankingu")
    @CfgComment("{G-KILLS} - suma zabojstw czlonkow gildii")
    @CfgComment("{G-DEATHS} - suma smierci czlonkow gildii")
    @CfgComment("{G-KDR} - stosunek zabojstw do smierci czlonkow gildii")
    @CfgComment("{G-MEMBERS-ONLINE} - liczba czlonkow gildii online")
    @CfgComment("{G-MEMBERS-ALL} - liczba wszystkich czlonkow gildii")
    @CfgComment("{G-VALIDITY} - data wygasniecia gildii")
    @CfgComment("{G-REGION-SIZE} - rozmiar gildii")
    @CfgComment("> Spis pozostalych zmiennych:")
    @CfgComment("{GUILDS} - liczba gildii na serwerze")
    @CfgComment("{USERS} - liczba uzytkownikow serwera")
    @CfgComment("{ONLINE} - liczba graczy online")
    @CfgComment("{TPS} - TPS serwera (wspierane tylko od wersji 1.8.8+ spigot/paperspigot)")
    @CfgComment("{SECOND} - Sekunda")
    @CfgComment("{MINUTE} - Minuta")
    @CfgComment("{HOUR} - Godzina")
    @CfgComment("{DAY_OF_WEEK} - Dzien tygodnia wyrazony w postaci nazwy dnia")
    @CfgComment("{DAY_OF_MONTH} - Dzien miesiaca wyrazony w postaci liczby")
    @CfgComment("{MONTH} - Miesiac wyrazony w postaci nazwy miesiaca")
    @CfgComment("{MONTH_NUMBER} - Miesiac wyrazony w postaci liczby")
    @CfgComment("{YEAR} - Rok")
    @CfgComment("{PTOP-<pozycja>} - Gracz na podanej pozycji w rankingu (np. {PTOP-1}, {PTOP-60})")
    @CfgComment("{GTOP-<pozycja>} - Gildia na podanej pozycji w rankingu (np. {GTOP-1}, {PTOP-50})")
    @CfgName("player-list")
    @CfgStringStyle(StringStyle.ALWAYS_QUOTED)
    public Map<Integer, String> playerList = ImmutableMap.<Integer, String>builder()
            .put(1, "&7Nick: &b{PLAYER}")
            .put(2, "&7Ping: &b{PING}")
            .put(3, "&7Punkty: &b{POINTS}")
            .put(4, "&7Zabojstwa: &b{KILLS}")
            .put(5, "&7Smierci: &b{DEATHS}")
            .put(6, "&7KDR: &b{KDR}")
            .put(7, "&7Gildia: &b{G-NAME}")
            .put(9, "&7TAG: &b{G-TAG}")
            .put(10, "&7Punkty gildii: &b{G-POINTS-FORMAT}")
            .put(11, "&7Pozycja gildii: &b{G-POSITION}")
            .put(12, "&7Liczba graczy online: &b{G-MEMBERS-ONLINE}")
            .put(21, "&7Online: &b{ONLINE}")
            .put(22, "&7TPS: &b{TPS}")
            .put(41, "&bTop 3 Gildii")
            .put(42, "&71. &b{GTOP-1}")
            .put(43, "&72. &b{GTOP-2}")
            .put(44, "&73. &b{GTOP-3}")
            .put(61, "&bTop 3 Graczy")
            .put(62, "&71. &b{PTOP-1}")
            .put(63, "&72. &b{PTOP-2}")
            .put(64, "&73. &b{PTOP-3}")
            .build();

    @CfgComment("Wyglad naglowka w liscie graczy.")
    @CfgName("player-list-header")
    @CfgStringStyle(StringStyle.ALWAYS_QUOTED)
    public String playerListHeader = "&7FunnyGuilds &b4.7.0 Tribute";

    @CfgComment("Wyglad stopki w liscie graczy.")
    @CfgName("player-list-footer")
    @CfgStringStyle(StringStyle.ALWAYS_QUOTED)
    public String playerListFooter = "&c&lWiadomosci braku (pokazujace sie, gdy gracz nie ma gildii) mozna zmienic w pliku messages.yml!";

    @CfgComment("Liczba pingu pokazana przy kazdej komorce.")
    @CfgName("player-list-ping")
    public int playerListPing = 0;

    @CfgComment("Czy wszystkie mozliwe komorki maja zostac zapelnione, nie zwazywszy na liczbe graczy online")
    @CfgName("player-list-fill-cells")
    public boolean playerListFillCells = true;

    @CfgComment("Czy tablista ma byc wlaczona")
    @CfgName("player-list-enable")
    public boolean playerListEnable = true;

    @CfgComment("Co ile tickow lista graczy powinna zostac odswiezona")
    @CfgName("player-list-update-interval")
    public int playerListUpdateInterval = 20;

    @CfgExclude
    public long playerListUpdateInterval_;

    @CfgComment("Czy zmienne typu {PTOP-%} oraz {GTOP-%} powinny byc pokolorowane w zaleznosci od relacji gildyjnych")
    @CfgName("player-list-use-relationship-colors")
    public boolean playerListUseRelationshipColors = false;

    @CfgComment("Czy tagi gildyjne obok nicku gracza maja byc wlaczone")
    @CfgName("guild-tag-enabled")
    public boolean guildTagEnabled = true;

    @CfgComment("Czy tag gildii podany przy tworzeniu gildii powinien zachowac forme taka, w jakiej zostal wpisany")
    @CfgComment("UWAGA: Gdy ta opcja jest wlaczona, opcja \"guild-tag-uppercase\" nie bedzie miala wplywu na tag gildii")
    @CfgName("guild-tag-keep-case")
    public boolean guildTagKeepCase = true;

    @CfgComment("Czy tagi gildii powinny byc pokazywane wielka litera")
    @CfgComment("Dziala dopiero po zrestartowaniu serwera")
    @CfgName("guild-tag-uppercase")
    public boolean guildTagUppercase = false;

    @CfgComment("Czy wlaczyc tlumaczenie nazw przedmiotow przy zabojstwie")
    @CfgName("translated-materials-enable")
    public boolean translatedMaterialsEnable = true;

    @CfgComment("Tlumaczenia nazw przedmiotow przy zabojstwie")
    @CfgComment("Wypisywac w formacie nazwa_przedmiotu: \"tlumaczona nazwa przedmiotu\"")
    @CfgName("translated-materials-name")
    @CfgStringStyle(StringStyle.ALWAYS_QUOTED)
    public Map<String, String> translatedMaterials_ = ImmutableMap.<String, String>builder()
            .put("diamond_sword", "&3diamentowy miecz")
            .put("iron_sword", "&7zelazny miecz")
            .build();

    @CfgExclude
    public Map<Material, String> translatedMaterials;

    @CfgComment("Czy filtry nazw i tagow gildii powinny byc wlaczone")
    @CfgName("check-for-restricted-guild-names")
    public boolean checkForRestrictedGuildNames = false;

    @CfgComment("Niedozwolone nazwy przy zakladaniu gildii")
    @CfgName("restricted-guild-names")
    @CfgStringStyle(StringStyle.ALWAYS_QUOTED)
    @CfgCollectionStyle(CollectionStyle.ALWAYS_NEW_LINE)
    public List<String> restrictedGuildNames = Collections.singletonList("Administracja");

    @CfgComment("Niedozwolone tagi przy zakladaniu gildii")
    @CfgName("restricted-guild-tags")
    @CfgStringStyle(StringStyle.ALWAYS_QUOTED)
    @CfgCollectionStyle(CollectionStyle.ALWAYS_NEW_LINE)
    public List<String> restrictedGuildTags = Collections.singletonList("TEST");

    @CfgComment("Czy powiadomienie o zabojstwie gracza powinno sie wyswietlac na title dla zabojcy")
    @CfgName("display-title-notification-for-killer")
    public boolean displayTitleNotificationForKiller = false;

    @CfgComment("Czy powiadomienia o wejsciu na teren gildii czlonka gildii powinny byc wyswietlane")
    @CfgName("notification-guild-member-display")
    public boolean regionEnterNotificationGuildMember = false;

    @CfgComment("Gdzie maja pojawiac sie wiadomosci zwiazane z poruszaniem sie po terenach gildii")
    @CfgComment("Mozliwe miejsca wyswietlania: ACTIONBAR, BOSSBAR, CHAT, TITLE")
    @CfgName("region-move-notification-style")
    @CfgStringStyle(StringStyle.ALWAYS_QUOTED)
    @CfgCollectionStyle(CollectionStyle.ALWAYS_NEW_LINE)
    public List<String> regionEnterNotificationStyle_ = Arrays.asList("ACTIONBAR", "BOSSBAR");

    @CfgExclude
    public List<NotificationStyle> regionEnterNotificationStyle = new ArrayList<>();

    @CfgComment("Jak dlugo title/subtitle powinien sie pojawiac")
    @CfgComment("Czas podawany w tickach (1 sekunda = 20 tickow)")
    @CfgComment("Opcja dziala tylko gdy aktywne jest powiadamianie w trybie TITLE")
    @CfgName("notification-title-fade-in")
    public int notificationTitleFadeIn = 10;

    @CfgComment("Jak dlugo title/subtitle powinien pozostac na ekranie gracza")
    @CfgComment("Czas podawany w tickach (1 sekunda = 20 tickow)")
    @CfgComment("Opcja dziala tylko gdy aktywne jest powiadamianie w trybie TITLE")
    @CfgName("notification-title-stay")
    public int notificationTitleStay = 10;

    @CfgComment("Jak dlugo title/subtitle powinien znikac")
    @CfgComment("Czas podawany w tickach (1 sekunda = 20 tickow)")
    @CfgComment("Opcja dziala tylko gdy aktywne jest powiadamianie w trybie TITLE")
    @CfgName("notification-title-fade-out")
    public int notificationTitleFadeOut = 10;

    @CfgComment("Jakiego koloru powinien byc boss bar podczas wyswietlania notyfikacji")
    @CfgComment("Dostepne kolory: PINK, BLUE, RED, GREEN, YELLOW, PURPLE, WHITE")
    @CfgName("notification-boss-bar-color")
    public String bossBarColor = "RED";

    @CfgComment("Jakiego stylu powinien byc boss bar podczas wyswietlania notyfikacji")
    @CfgComment("Dostepne style: SOLID, SEGMENTED_6, SEGMENTED_10, SEGMENTED_12, SEGMENTED_20")
    @CfgName("notification-boss-bar-style")
    public String bossBarStyle = "SOLID";

    @CfgComment("Jakie flagi powinny byc nalozone na byc boss bar podczas wyswietlania notyfikacji")
    @CfgComment("Dostepne flagi: DARKEN_SKY, PLAY_BOSS_MUSIC, CREATE_FOG")
    @CfgName("notification-boss-bar-flags")
    public List<String> bossBarFlags = Collections.singletonList("CREATE_FOG");

    @CfgExclude
    public BossBarOptions bossBarOptions_;

    @CfgComment("Czy osoba, ktora zalozyla pierwsza gildie na serwerze powinna dostac nagrode")
    @CfgName("should-give-rewards-for-first-guild")
    public boolean giveRewardsForFirstGuild = false;

    @CfgComment("Przedmioty, ktore zostana nadane graczowi, ktory pierwszy zalozyl gildie na serwerze")
    @CfgComment("Dziala tylko w wypadku, gdy opcja \"should-give-rewards-for-first-guild\" jest wlaczona")
    @CfgName("rewards-for-first-guild")
    @CfgStringStyle(StringStyle.ALWAYS_QUOTED)
    @CfgCollectionStyle(CollectionStyle.ALWAYS_NEW_LINE)
    public List<String> firstGuildRewards_ = Collections.singletonList("1 diamond name:&bNagroda_za_pierwsza_gildie_na_serwerze");

    @CfgExclude
    public List<ItemStack> firstGuildRewards;

    @CfgComment("Zbior przedmiotow potrzebnych do resetu rankingu")
    @CfgName("rank-reset-needed-items")
    @CfgStringStyle(StringStyle.ALWAYS_QUOTED)
    @CfgCollectionStyle(CollectionStyle.ALWAYS_NEW_LINE)
    public List<String> rankResetItems_ = Collections.singletonList("1 diamond");

    @CfgExclude
    public List<ItemStack> rankResetItems;

    @CfgComment("Czy przy szukaniu danych o graczu ma byc pomijana wielkosc znakow")
    @CfgName("player-lookup-ignorecase")
    public boolean playerLookupIgnorecase = false;

    @CfgComment("Nazwy komend")
    @CfgName("commands")
    public Commands commands = new Commands();

    @CfgComment("Czy event PlayMoveEvent ma byc aktywny (odpowiada za wyswietlanie powiadomien o wejsciu na teren gildii)")
    @CfgName("event-move")
    public boolean eventMove = true;

    @CfgExclude
    public boolean eventPhysics;

    @CfgComment("Ilość wątków używanych przez ConcurrencyManager")
    @CfgName("concurrency-threads")
    public int concurrencyThreads = 1;

    @CfgComment("Co ile minut ma automatycznie zapisywac dane")
    @CfgName("data-interval")
    public int dataInterval = 1;

    @CfgComment("Jak dlugo plugin powinien czekac na zatrzymanie wszystkich biezacych zadan przy wylaczaniu pluginu")
    @CfgComment("Czas podawany w sekundach")
    @CfgName("plugin-task-termination-timeout")
    public long pluginTaskTerminationTimeout_ = 30;

    @CfgExclude
    public long pluginTaskTerminationTimeout;

    @CfgComment("Typ zapisu danych")
    @CfgComment("FLAT - Lokalne pliki")
    @CfgComment("MYSQL - baza danych")
    @CfgName("data-model")
    public DataModel dataModel = DataModel.FLAT;

    @CfgComment("Dane wymagane do polaczenia z baza")
    @CfgComment("UWAGA: connectionTimeout jest w milisekundach!")
    @CfgComment("Sekcja poolSize odpowiada za liczbe zarezerwowanych polaczen, domyslna wartosc 5 powinna wystarczyc")
    @CfgComment("Aby umozliwic FG automatyczne zarzadzanie liczba polaczen - ustaw poolSize na -1")
    @CfgComment("Sekcje usersTableName, guildsTableName i regionsTableName to nazwy tabel z danymi FG w bazie danych")
    @CfgComment("Najlepiej zmieniac te nazwy tylko wtedy, gdy jest naprawde taka potrzeba (np. wystepuje konflikt z innym pluginem)")
    @CfgComment("Aby zmienic nazwy tabel, gdy masz juz w bazie jakies dane z FG:")
    @CfgComment("1. Wylacz serwer")
    @CfgComment("2. Zmien dane w configu FG")
    @CfgComment("3. Zmien nazwy tabel w bazie uzywajac np. phpMyAdmin")
    @CfgName("mysql")
    public MySQL mysql = new MySQL("localhost", 3306, "db", "root", "passwd", 5, 30000, true, "users", "guilds", "regions");

    @CfgName("redis")
    public RedisConfig redisConfig = new RedisConfig(false, "localhost", 6379, "passwd", 2000, 0);

    private List<ItemStack> loadItemStackList(List<String> strings) {
        List<ItemStack> items = new ArrayList<>();
        for (String item : strings) {
            if (item == null || "".equals(item)) {
                continue;
            }

            ItemStack itemstack = ItemUtils.parseItem(item);
            if (itemstack != null) {
                items.add(itemstack);
            }
        }

        return items;
    }

    private List<ItemStack> loadGUI(List<String> contents) {
        List<ItemStack> items = new ArrayList<>();

        for (String var : contents) {
            ItemStack item = null;

            if (var.contains("GUI-")) {
                int index = RankUtils.getIndex(var);

                if (index > 0 && index <= items.size()) {
                    item = items.get(index - 1);
                }
            }
            else if (var.contains("VIPITEM-")) {
                try {
                    int index = RankUtils.getIndex(var);

                    if (index > 0 && index <= createItemsVip.size()) {
                        item = createItemsVip.get(index - 1);
                    }
                }
                catch (IndexOutOfBoundsException e) {
                    FunnyGuilds.getInstance().getPluginLogger().parser("Index given in " + var + " is > " + createItemsVip.size() + " or <= 0");
                }
            }
            else if (var.contains("ITEM-")) {
                try {
                    int index = RankUtils.getIndex(var);

                    if (index > 0 && index <= createItems.size()) {
                        item = createItems.get(index - 1);
                    }
                }
                catch (IndexOutOfBoundsException e) {
                    FunnyGuilds.getInstance().getPluginLogger().parser("Index given in " + var + " is > " + createItems.size() + " or <= 0");
                }
            }
            else {
                item = ItemUtils.parseItem(var);
            }

            if (item == null) {
                item = new ItemBuilder(MaterialUtils.matchMaterial("stained_glass_pane"), 1, 14).setName("&c&lERROR IN GUI CREATION: " + var, true).getItem();
            }

            items.add(item);
        }

        return items;
    }

    public void load() {
        this.dateFormat = new SimpleDateFormat(FunnyGuilds.getInstance().getMessageConfiguration().dateFormat);

        try {
            this.nameRegex = GuildRegex.valueOf(this.nameRegex_.toUpperCase());
        }
        catch (Exception e) {
            this.nameRegex = GuildRegex.LETTERS;
            FunnyGuilds.getInstance().getPluginLogger().error("\"" + this.nameRegex_ + "\" is not a valid regex option!");
        }

        try {
            this.tagRegex = GuildRegex.valueOf(this.tagRegex_.toUpperCase());
        }
        catch (Exception e) {
            this.tagRegex = GuildRegex.LETTERS;
            FunnyGuilds.getInstance().getPluginLogger().error("\"" + this.tagRegex_ + "\" is not a valid regex option!");
        }

        this.createItems = loadItemStackList(this.items_);
        this.createItemsVip = loadItemStackList(this.itemsVip_);

        this.guiItems = loadGUI(this.guiItems_);

        if (! useCommonGUI) {
            this.guiItemsVip = loadGUI(this.guiItemsVip_);
        }

        this.guiItemsTitle = ChatUtils.colored(this.guiItemsTitle_);
        this.guiItemsVipTitle = ChatUtils.colored(this.guiItemsVipTitle_);
        this.guiItemsLore = ChatUtils.colored(this.guiItemsLore_);

        try {
            this.createEntityType = EntityType.valueOf(this.createType.toUpperCase().replace(" ", "_"));
        }
        catch (Exception materialThen) {
            this.createMaterial = MaterialUtils.parseMaterialData(this.createType, true);
        }

        if (this.createMaterial != null && MaterialUtils.hasGravity(this.createMaterial.getLeft())) {
            this.eventPhysics = true;
        }

        if (this.enlargeEnable) {
            this.enlargeItems = this.loadItemStackList(this.enlargeItems_);
        }
        else {
            this.enlargeSize = 0;
            this.enlargeItems = null;
        }

        if (this.buggedBlocksTimer < 0L) {
            FunnyGuilds.getInstance().getPluginLogger().error("The field named \"bugged-blocks-timer\" can not be less than zero!");
            this.buggedBlocksTimer = 20L; // default value
        }

        this.blockedInteract = new HashSet<>();

        for (String s : this._blockedInteract) {
            this.blockedInteract.add(MaterialUtils.parseMaterial(s, false));
        }

        this.buggedBlocksExclude = new HashSet<>();

        for (String s : this.buggedBlocksExclude_) {
            this.buggedBlocksExclude.add(MaterialUtils.parseMaterial(s, false));
        }

        try {
            this.rankSystem = RankSystem.valueOf(this.rankSystem_.toUpperCase());
        }
        catch (Exception ex) {
            this.rankSystem = RankSystem.ELO;
            FunnyGuilds.getInstance().getPluginLogger().error("\"" + this.rankSystem_ + "\" is not a valid rank system!");
        }

        if (this.rankSystem == RankSystem.ELO) {
            Map<IntegerRange, Integer> parsedData = new HashMap<>();

            for (Entry<IntegerRange, String> entry : IntegerRange.parseIntegerRange(this.eloConstants_, false).entrySet()) {
                try {
                    parsedData.put(entry.getKey(), Integer.parseInt(entry.getValue()));
                }
                catch (NumberFormatException e) {
                    FunnyGuilds.getInstance().getPluginLogger().parser("\"" + entry.getValue() + "\" is not a valid elo constant!");
                }
            }

            this.eloConstants = parsedData;
        }

        Map<Material, Double> map = new EnumMap<>(Material.class);

        for (Map.Entry<String, Double> entry : this.explodeMaterials_.entrySet()) {
            double chance = entry.getValue();

            if (chance < 0) {
                continue;
            }

            if (entry.getKey().equalsIgnoreCase("*")) {
                this.allMaterialsAreExplosive = true;
                this.defaultExplodeChance = chance;
                continue;
            }

            Material material = MaterialUtils.parseMaterial(entry.getKey(), true);

            if (material == null || material == Material.AIR) {
                continue;
            }

            map.put(material, chance);
        }

        this.explodeMaterials = map;

        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        this.guildTNTProtectionStartTime = LocalTime.parse(guildTNTProtectionStartTime_, timeFormatter);
        this.guildTNTProtectionEndTime = LocalTime.parse(guildTNTProtectionEndTime_, timeFormatter);
        this.guildTNTProtectionOrMode = this.guildTNTProtectionStartTime.isAfter(this.guildTNTProtectionEndTime);
        this.translatedMaterials = new HashMap<>();

        for (String materialName : translatedMaterials_.keySet()) {
            Material material = MaterialUtils.matchMaterial(materialName.toUpperCase());

            if (material == null) {
                continue;
            }

            translatedMaterials.put(material, translatedMaterials_.get(materialName));
        }

        for (String s : this.regionEnterNotificationStyle_) {
            this.regionEnterNotificationStyle.add(NotificationStyle.valueOf(s.toUpperCase()));
        }

        if (this.notificationTitleFadeIn <= 0) {
            FunnyGuilds.getInstance().getPluginLogger().error("The field named \"notification-title-fade-in\" can not be less than or equal to zero!");
            this.notificationTitleFadeIn = 10;
        }

        if (this.notificationTitleStay <= 0) {
            FunnyGuilds.getInstance().getPluginLogger().error("The field named \"notification-title-stay\" can not be less than or equal to zero!");
            this.notificationTitleStay = 10;
        }

        if (this.notificationTitleFadeOut <= 0) {
            FunnyGuilds.getInstance().getPluginLogger().error("The field named \"notification-title-fade-out\" can not be less than or equal to zero!");
            this.notificationTitleFadeOut = 10;
        }

        if (! "v1_8_R1".equals(Reflections.SERVER_VERSION) && ! "v1_8_R3".equals(Reflections.SERVER_VERSION)) {
            this.bossBarOptions_ = BossBarOptions.builder()
                    .color(this.bossBarColor)
                    .style(this.bossBarStyle)
                    .flags(this.bossBarFlags)
                    .build();
        }

        this.rankResetItems = loadItemStackList(this.rankResetItems_);

        this.firstGuildRewards = loadItemStackList(this.firstGuildRewards_);

        this.warProtection = TimeUtils.parseTime(this.warProtection_);
        this.warWait = TimeUtils.parseTime(this.warWait_);

        this.validityStart = TimeUtils.parseTime(this.validityStart_);
        this.validityTime = TimeUtils.parseTime(this.validityTime_);
        this.validityWhen = TimeUtils.parseTime(this.validityWhen_);

        this.validityItems = this.loadItemStackList(this.validityItems_);

        this.joinItems = this.loadItemStackList(this.joinItems_);
        this.baseItems = this.loadItemStackList(this.baseItems_);

        this.prefixOur = ChatUtils.colored(this.prefixOur_);
        this.prefixAllies = ChatUtils.colored(this.prefixAllies_);
        this.prefixOther = ChatUtils.colored(this.prefixOther_);

        this.ptopOnline = ChatUtils.colored(this.ptopOnline_);
        this.ptopOffline = ChatUtils.colored(this.ptopOffline_);

        this.dummySuffix = ChatUtils.colored(this.dummySuffix_);

        this.chatPosition = ChatUtils.colored(this.chatPosition_);
        this.chatGuild = ChatUtils.colored(this.chatGuild_);
        this.chatRank = ChatUtils.colored(this.chatRank_);
        this.chatPoints = ChatUtils.colored(this.chatPoints_);

        this.pointsFormat = IntegerRange.parseIntegerRange(this.pointsFormat_, true);
        this.pingFormat = IntegerRange.parseIntegerRange(this.pingFormat_, true);

        this.ptopPoints = ChatUtils.colored(this.ptopPoints_);
        this.gtopPoints = ChatUtils.colored(this.gtopPoints_);

        this.chatPrivDesign = ChatUtils.colored(this.chatPrivDesign_);
        this.chatAllyDesign = ChatUtils.colored(this.chatAllyDesign_);
        this.chatGlobalDesign = ChatUtils.colored(this.chatGlobalDesign_);

        if (this.pasteSchematicOnCreation) {
            if (this.guildSchematicFileName == null || this.guildSchematicFileName.isEmpty()) {
                FunnyGuilds.getInstance().getPluginLogger().error("The field named \"guild-schematic-file-name\" is empty, but field \"paste-schematic-on-creation\" is set to true!");
                this.pasteSchematicOnCreation = false;
            }
            else {
                this.guildSchematicFile = new File(FunnyGuilds.getInstance().getDataFolder(), this.guildSchematicFileName);

                if (! this.guildSchematicFile.exists()) {
                    FunnyGuilds.getInstance().getPluginLogger().error("File with given name in field \"guild-schematic-file-name\" does not exist!");
                    this.pasteSchematicOnCreation = false;
                }
            }
        }

        this.playerListUpdateInterval_ = Math.max(1, this.playerListUpdateInterval);
        this.lastAttackerAsKillerConsiderationTimeout_ = TimeUnit.SECONDS.toMillis(this.lastAttackerAsKillerConsiderationTimeout);

        this.rankingUpdateInterval_ = Math.max(1, this.rankingUpdateInterval);
        this.pluginTaskTerminationTimeout = Math.max(1, this.pluginTaskTerminationTimeout_);
    }

    public static class Commands {
        public FunnyCommand funnyguilds = new FunnyCommand("funnyguilds", Collections.singletonList("fg"));

        public FunnyCommand guild     = new FunnyCommand("gildia", Arrays.asList("gildie", "g"));
        public FunnyCommand create    = new FunnyCommand("zaloz");
        public FunnyCommand delete    = new FunnyCommand("usun");
        public FunnyCommand confirm   = new FunnyCommand("potwierdz");
        public FunnyCommand invite    = new FunnyCommand("zapros");
        public FunnyCommand join      = new FunnyCommand("dolacz");
        public FunnyCommand leave     = new FunnyCommand("opusc");
        public FunnyCommand kick      = new FunnyCommand("wyrzuc");
        public FunnyCommand base      = new FunnyCommand("baza");
        public FunnyCommand enlarge   = new FunnyCommand("powieksz");
        public FunnyCommand ally      = new FunnyCommand("sojusz");
        public FunnyCommand items     = new FunnyCommand("przedmioty");
        public FunnyCommand escape    = new FunnyCommand("ucieczka", Collections.singletonList("escape"));
        public FunnyCommand rankReset = new FunnyCommand("rankreset", Collections.singletonList("resetrank"));

        @CfgName("break")
        public FunnyCommand break_ = new FunnyCommand("rozwiaz");

        public FunnyCommand info     = new FunnyCommand("info");
        public FunnyCommand player   = new FunnyCommand("gracz");
        public FunnyCommand top      = new FunnyCommand("top", Collections.singletonList("top10"));
        public FunnyCommand validity = new FunnyCommand("przedluz");
        public FunnyCommand leader   = new FunnyCommand("lider", Collections.singletonList("zalozyciel"));
        public FunnyCommand deputy   = new FunnyCommand("zastepca");
        public FunnyCommand ranking  = new FunnyCommand("ranking");
        public FunnyCommand setbase  = new FunnyCommand("ustawbaze", Collections.singletonList("ustawdom"));
        public FunnyCommand pvp      = new FunnyCommand("pvp", Collections.singletonList("ustawpvp"));

        @CfgComment("Komendy administratora")
        public AdminCommands admin = new AdminCommands();

        public static class FunnyCommand {
            @CfgStringStyle(StringStyle.ALWAYS_QUOTED)
            public String name;

            @CfgStringStyle(StringStyle.ALWAYS_QUOTED)
            @CfgCollectionStyle(CollectionStyle.ALWAYS_NEW_LINE)
            public List<String> aliases;

            public boolean enabled;

            public FunnyCommand() {
            }

            public FunnyCommand(String name) {
                this(name, Collections.emptyList(), true);
            }

            public FunnyCommand(String name, List<String> aliases) {
                this(name, aliases, true);
            }

            public FunnyCommand(String name, List<String> aliases, boolean enabled) {
                this.name = name;
                this.aliases = aliases;
                this.enabled = enabled;
            }
        }

        public static class AdminCommands {
            @CfgStringStyle(StringStyle.ALWAYS_QUOTED)
            public String main = "ga";

            @CfgStringStyle(StringStyle.ALWAYS_QUOTED)
            public String add = "ga dodaj";

            @CfgStringStyle(StringStyle.ALWAYS_QUOTED)
            public String delete = "ga usun";

            @CfgStringStyle(StringStyle.ALWAYS_QUOTED)
            public String kick = "ga wyrzuc";

            @CfgStringStyle(StringStyle.ALWAYS_QUOTED)
            public String teleport = "ga tp";

            @CfgStringStyle(StringStyle.ALWAYS_QUOTED)
            public String points = "ga points";

            @CfgStringStyle(StringStyle.ALWAYS_QUOTED)
            public String kills = "ga kills";

            @CfgStringStyle(StringStyle.ALWAYS_QUOTED)
            public String deaths = "ga deaths";

            @CfgStringStyle(StringStyle.ALWAYS_QUOTED)
            public String ban = "ga ban";

            @CfgStringStyle(StringStyle.ALWAYS_QUOTED)
            public String lives = "ga zycia";

            @CfgStringStyle(StringStyle.ALWAYS_QUOTED)
            public String move = "ga przenies";

            @CfgStringStyle(StringStyle.ALWAYS_QUOTED)
            public String unban = "ga unban";

            @CfgStringStyle(StringStyle.ALWAYS_QUOTED)
            public String validity = "ga przedluz";

            @CfgStringStyle(StringStyle.ALWAYS_QUOTED)
            public String name = "ga nazwa";

            @CfgStringStyle(StringStyle.ALWAYS_QUOTED)
            public String tag = "ga tag";

            @CfgStringStyle(StringStyle.ALWAYS_QUOTED)
            public String spy = "ga spy";

            @CfgStringStyle(StringStyle.ALWAYS_QUOTED)
            public String enabled = "ga enabled";

            @CfgStringStyle(StringStyle.ALWAYS_QUOTED)
            public String leader = "ga lider";

            @CfgStringStyle(StringStyle.ALWAYS_QUOTED)
            public String deputy = "ga zastepca";

            @CfgStringStyle(StringStyle.ALWAYS_QUOTED)
            public String protection = "ga ochrona";

            @CfgStringStyle(StringStyle.ALWAYS_QUOTED)
            public String base = "ga baza";
        }
    }

    public enum DataModel {
        FLAT,
        MYSQL
    }

    public static class MySQL {
        @CfgStringStyle(StringStyle.ALWAYS_QUOTED)
        public String hostname;

        public int port;

        @CfgStringStyle(StringStyle.ALWAYS_QUOTED)
        public String database;

        @CfgStringStyle(StringStyle.ALWAYS_QUOTED)
        public String user;

        @CfgStringStyle(StringStyle.ALWAYS_QUOTED)
        public String password;

        public int     poolSize;
        public int     connectionTimeout;
        public boolean useSSL;

        @CfgStringStyle(StringStyle.ALWAYS_QUOTED)
        public String usersTableName;

        @CfgStringStyle(StringStyle.ALWAYS_QUOTED)
        public String guildsTableName;

        @CfgStringStyle(StringStyle.ALWAYS_QUOTED)
        public String regionsTableName;

        public MySQL() {
        }

        public MySQL(String hostname, int port, String database, String user, String password, int poolSize, int connectionTimeout, boolean useSSL, String usersTableName, String guildsTableName, String regionsTableName) {
            this.hostname = hostname;
            this.port = port;
            this.database = database;
            this.user = user;
            this.password = password;
            this.poolSize = poolSize;
            this.connectionTimeout = connectionTimeout;
            this.useSSL = useSSL;
            this.usersTableName = usersTableName;
            this.guildsTableName = guildsTableName;
            this.regionsTableName = regionsTableName;
        }
    }
    public static class RedisConfig {

        public boolean enabled;

        @CfgStringStyle(StringStyle.ALWAYS_QUOTED)
        public String host;

        public int port;


        @CfgStringStyle(StringStyle.ALWAYS_QUOTED)
        public String password;

        public int connectionTimeout;
        public int operationTimeout;
        public int database;

        public RedisConfig() {
        }

        public RedisConfig(boolean enabled, String host, int port, String password, int connectionTimeout, int database) {
            this.enabled = enabled;
            this.host = host;
            this.port = port;
            this.password = password;
            this.connectionTimeout = connectionTimeout;
            this.database = database;
        }
    }
}
